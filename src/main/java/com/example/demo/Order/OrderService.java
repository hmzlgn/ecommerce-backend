package com.example.demo.Order;

import com.example.demo.StockMovement.StockMovementCreateDto;
import com.example.demo.Product.Product;
import com.example.demo.Product.ProductRepository;
import com.example.demo.StockMovement.StockMovementService;
import com.example.demo.User.User;
import com.example.demo.User.UserRepository;
import com.example.demo.enums.OrderStatus;
import com.example.demo.enums.PaymentStatus;
import com.example.demo.enums.StockMovementType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final StockMovementService stockMovementService;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public Order createOrder(OrderCreateDto request){
        //1.Kullanıcı veritabanında var mı kontrol et.
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Kullanıcı Bulunamadı! ID:" + request.getUserId()));
        //2.Boş bir Order tepsisi oluştur.
        Order order = new Order();

        order.setUser(user);
        order.setShippingAddress(request.getShippingAddress());
        order.setStatus(OrderStatus.PENDING);
        order.setPaymentStatus(PaymentStatus.PENDING);

        //3.Geçiçi olarak Order(Sipariş) toplam fiyatını 0 a eşitle.
        BigDecimal totalAmount = BigDecimal.ZERO;

        //4.Eşitlediğin tutarı order'ın totalAmountuna set et.
        order.setTotalAmount(totalAmount);

        //5.OrderItem(Sepetteki ürün satırı) için liste oluştur
        List<OrderItem> orderItemList=new ArrayList<>();

        //6.Döngü içerisinde hem ürün stok kontrolü hem de request içerisinde gelen ürünleri listeye ekle.
        for (OrderItemRequestDto items: request.getItems()){
            Product product =productRepository.findById(items.getProductId())
                    .orElseThrow(() -> new RuntimeException("Ürün Bulunamadı! ID: " + items.getProductId()));
            //Stok kontrolü
            if (items.getQuantity()>product.getStock()){
                throw new RuntimeException("Ürün adedi, stok adedinden fazla olamaz!" +
                        "\nStok:" + product.getStock() +
                        "\nSipariş edilmek istenen ürün adedi:" + items.getQuantity());
            }
            //7.Stokta ve kullanıcıda sorun yoksa ürünün stoğunu düşür.
            product.setStock(product.getStock()-items.getQuantity());
            product=productRepository.save(product);
            //8.StokMovementCreateDto yu oluştur/set et.
            StockMovementCreateDto stockMovement = new StockMovementCreateDto();
            stockMovement.setProductId(items.getProductId());
            stockMovement.setQuantity(items.getQuantity());
            stockMovement.setMovementType(StockMovementType.SALE);
            //9.Set ettiklerini StockMovementService ile kaydet.
            stockMovementService.recordMovement(stockMovement);

            //10.OrderItem türünden bir boş nesne oluştur.
            //İçerisine Siparişi, Ürünü, ürünün O ANKİ adını, O ANKİ fiyatını ve adedini set et.
            OrderItem orderItem=new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setProductName(product.getName());
            orderItem.setUnitPrice(product.getSellPrice());
            orderItem.setQuantity(items.getQuantity());
            //
            BigDecimal itemTotal = orderItem.getUnitPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity()));
            totalAmount=totalAmount.add(itemTotal);
            orderItemList.add(orderItem);
        }
        // Döngü bitti, fişi koliye zımbala
        order.setOrderItems(orderItemList);

        // Genel toplamı koliye yaz
        order.setTotalAmount(totalAmount);

        // Koliyi veritabanına kaydet ve işlemi bitir!
        return order=orderRepository.save(order);
    }
}