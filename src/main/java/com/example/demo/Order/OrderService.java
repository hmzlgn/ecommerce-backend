package com.example.demo.Order;

import com.example.demo.OrderItem.OrderItem;
import com.example.demo.OrderItem.OrderItemRequestDto;
import com.example.demo.OrderItem.OrderItemResponseDto;
import com.example.demo.Product.Product;
import com.example.demo.Product.ProductRepository;
import com.example.demo.User.User;
import com.example.demo.User.UserRepository;
import com.example.demo.enums.OrderStatus;
import com.example.demo.enums.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public OrderService(OrderRepository orderRepository,
                        ProductRepository productRepository,
                        UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    // --- 1. SİPARİŞ OLUŞTURMA (MÜŞTERİ SEPETİ ONAYLADIĞINDA) ---
    public OrderResponseDto createOrder(Long userId, OrderCreateDto request) {
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new IllegalArgumentException("Sipariş en az bir ürün içermelidir!");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı!"));

        Order order = new Order();
        order.setUser(user);
        order.setOrderStatus(OrderStatus.PENDING);
        order.setOrderItems(new ArrayList<>());
        order.setShippingAddress(request.getShippingAddress());
        order.setPaymentMethod(request.getPaymentMethod());
        order.setPaymentStatus(PaymentStatus.PENDING);
        order.setOrderNote(request.getOrderNote());
        // orderDate set edilmiyor: @CreationTimestamp bunu otomatik dolduruyor

        BigDecimal totalAmount = BigDecimal.ZERO;

        // Deadlock riskini azaltmak için ürünleri productId'ye göre sıralı işliyoruz
        List<OrderItemRequestDto> sortedItems = request.getItems().stream()
                .sorted(Comparator.comparing(OrderItemRequestDto::getProductId))
                .collect(Collectors.toList());

        for (OrderItemRequestDto itemDto : sortedItems) {
            if (itemDto.getQuantity() == null || itemDto.getQuantity() <= 0) {
                throw new IllegalArgumentException("Ürün miktarı geçerli değil! Ürün ID: " + itemDto.getProductId());
            }

            // Pessimistic lock: aynı ürün için eşzamanlı stok düşürmeyi engeller
            Product product = productRepository.findByIdForUpdate(itemDto.getProductId())
                    .orElseThrow(() -> new RuntimeException("Ürün bulunamadı! ID: " + itemDto.getProductId()));

            if (product.getStock() < itemDto.getQuantity()) {
                throw new RuntimeException("Yetersiz stok! Ürün: " + product.getName()
                        + " | Kalan Stok: " + product.getStock());
            }

            // Stok Düşme İşlemi
            product.setStock(product.getStock() - itemDto.getQuantity());
            productRepository.save(product);

            // OrderItem Oluşturma
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(itemDto.getQuantity());
            orderItem.setUnitPrice(product.getSellPrice()); // O anki satış fiyatını sabitlemek e-ticarette kanundur
            orderItem.setProductName(product.getName());

            BigDecimal itemQuantity = BigDecimal.valueOf(itemDto.getQuantity());
            BigDecimal itemTotalPrice = product.getSellPrice().multiply(itemQuantity);

            order.getOrderItems().add(orderItem);
            totalAmount = totalAmount.add(itemTotalPrice);
        }

        order.setTotalAmount(totalAmount);
        order = orderRepository.save(order);

        return mapToResponse(order);
    }

    // --- 2. SİPARİŞ DURUM GÜNCELLEMELERİ (DURUM MAKİNESİ) ---

    // Satıcı onayladığında çalışır (PENDING -> PROCESSING)
    public OrderResponseDto approveOrder(Long orderId) {
        Order order = getOrderEntity(orderId);

        if (order.getOrderStatus() != OrderStatus.PENDING) {
            throw new RuntimeException("Sadece 'Bekleyen' (PENDING) siparişler onaylanabilir!");
        }

        order.setOrderStatus(OrderStatus.PROCESSING);
        order = orderRepository.save(order);
        return mapToResponse(order);
    }

    // Kargoya verildiğinde çalışır (PROCESSING -> SHIPPED)
    public OrderResponseDto shipOrder(Long orderId) {
        Order order = getOrderEntity(orderId);

        if (order.getOrderStatus() != OrderStatus.PROCESSING) {
            throw new RuntimeException("Sipariş henüz satıcı tarafından onaylanmamış veya zaten kargolanmış!");
        }

        order.setOrderStatus(OrderStatus.SHIPPED);
        order = orderRepository.save(order);
        return mapToResponse(order);
    }

    // Müşteriye teslim edildiğinde çalışır (SHIPPED -> DELIVERED)
    public OrderResponseDto deliverOrder(Long orderId) {
        Order order = getOrderEntity(orderId);

        if (order.getOrderStatus() != OrderStatus.SHIPPED) {
            throw new RuntimeException("Sipariş henüz kargolanmamış!");
        }

        order.setOrderStatus(OrderStatus.DELIVERED);
        order = orderRepository.save(order);
        return mapToResponse(order);
    }

    // --- 3. SİPARİŞ İPTALİ VE STOK İADESİ ---
    public OrderResponseDto cancelOrder(Long orderId) {
        Order order = getOrderEntity(orderId);

        if (order.getOrderStatus() == OrderStatus.SHIPPED || order.getOrderStatus() == OrderStatus.DELIVERED) {
            throw new RuntimeException("Kargoya verilmiş siparişler doğrudan iptal edilemez! İade (Refund) faturası kesilmelidir.");
        }
        if (order.getOrderStatus() == OrderStatus.CANCELLED) {
            throw new RuntimeException("Bu sipariş zaten iptal edilmiş!");
        }

        // İptal edilen ürünlerin stoğunu depoya geri ekleme operasyonu
        // Burada da deadlock'tan kaçınmak için ürünleri sıralı işliyoruz
        List<OrderItem> sortedOrderItems = order.getOrderItems().stream()
                .sorted(Comparator.comparing(oi -> oi.getProduct().getId()))
                .collect(Collectors.toList());

        for (OrderItem item : sortedOrderItems) {
            Product product = productRepository.findByIdForUpdate(item.getProduct().getId())
                    .orElseThrow(() -> new RuntimeException("Ürün bulunamadı! ID: " + item.getProduct().getId()));
            product.setStock(product.getStock() + item.getQuantity());
            productRepository.save(product);
        }

        order.setOrderStatus(OrderStatus.CANCELLED);
        order = orderRepository.save(order);
        return mapToResponse(order);
    }

    // --- 4. LİSTELEME VE GETİRME İŞLEMLERİ (SAYFALAMA İLE) ---

    @Transactional(readOnly = true)
    public OrderResponseDto getOrderById(Long orderId) {
        return mapToResponse(getOrderEntity(orderId));
    }

    @Transactional(readOnly = true)
    public Page<OrderResponseDto> getOrdersByUser(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("orderDate").descending());
        Page<Order> orderPage = orderRepository.findByUserId(userId, pageable);
        return orderPage.map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public Page<OrderResponseDto> getAllOrders(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("orderDate").descending());
        Page<Order> orderPage = orderRepository.findAll(pageable);
        return orderPage.map(this::mapToResponse);
    }

    // --- 5. YARDIMCI METOTLAR ---

    // Kod tekrarını önlemek için merkezi Entity getirme metodu
    private Order getOrderEntity(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Sipariş bulunamadı! ID: " + orderId));
    }

    // Order -> OrderResponseDto paketleme görevlisi
    private OrderResponseDto mapToResponse(Order order) {
        OrderResponseDto response = new OrderResponseDto();
        response.setId(order.getId());
        response.setUserId(order.getUser().getId());
        response.setOrderDate(order.getOrderDate());
        response.setStatus(order.getOrderStatus());
        response.setTotalAmount(order.getTotalAmount());

        if (order.getOrderItems() != null) {
            List<OrderItemResponseDto> itemDtos = order.getOrderItems().stream()
                    .map(this::mapItemToResponse)
                    .collect(Collectors.toList());
            response.setItems(itemDtos);
        }

        return response;
    }

    // OrderItem -> OrderItemResponseDto paketleme görevlisi
    private OrderItemResponseDto mapItemToResponse(OrderItem item) {
        OrderItemResponseDto dto = new OrderItemResponseDto();
        dto.setId(item.getId());
        dto.setProductId(item.getProduct().getId());
        dto.setProductName(item.getProduct().getName());
        dto.setQuantity(item.getQuantity());
        dto.setUnitPrice(item.getUnitPrice());
        return dto;
    }
}