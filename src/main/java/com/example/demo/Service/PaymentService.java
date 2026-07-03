package com.example.demo.Service;

import com.example.demo.DTO.PaymentRequestDto;
import com.example.demo.DTO.PaymentResponseDto;
import com.example.demo.Entity.Order;
import com.example.demo.Entity.Payment;
import com.example.demo.Repository.OrderRepository;
import com.example.demo.Repository.PaymentRepository;
import com.example.demo.enums.OrderStatus;
import com.example.demo.enums.PaymentStatus;
import jakarta.transaction.Transactional;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;

    @Transactional
    public PaymentResponseDto processPayment(PaymentRequestDto request){
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(()-> new RuntimeException("Sipariş Bulunamadı! ID:" + request.getOrderId()));

        if (request.getAmount().compareTo(order.getTotalAmount()) != 0){
            throw new RuntimeException("Ödeme Tutarı ile sipariş tutarı eşleşmiyor!");
        }
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setAmount(request.getAmount());
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setPaymentStatus(PaymentStatus.SUCCESS);
        payment.setPaymentDate(LocalDateTime.now());
        //şimdilik bankadan gelen numarayı random veriyoruz.
        payment.setTransactionId(UUID.randomUUID().toString());

        order.setOrderStatus(OrderStatus.PAID);

        orderRepository.save(order);
        paymentRepository.save(payment);

        PaymentResponseDto response = new PaymentResponseDto();
        response.setId(payment.getId());
        response.setOrderId(order.getId());
        response.setPaymentStatus(payment.getPaymentStatus().toString());
        response.setPaymentDate(payment.getPaymentDate());
        response.setTransactionId(payment.getTransactionId());
        response.setAmount(request.getAmount());

        return response;
    }
}
