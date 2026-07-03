package com.example.demo.Entity;

import com.example.demo.enums.InvoiceType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "invoices")
@Getter
@Setter
@NoArgsConstructor
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name="order_id", nullable = false,unique = true)
    private Order order;

    @Column(name = "invoice_number", nullable = false, unique = true)
    private String invoiceNumber;

    @Column(name = "total_tax_amount", nullable = false)
    private BigDecimal totalTaxAmount;

    @Column(name = "total_amount_with_tax", nullable = false)
    private BigDecimal totalAmountWithTax;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime invoiceCreatedAt;

    @Column(nullable = false)
    private String billingAddress;

    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvoiceType invoiceType=InvoiceType.SALES; //default

    @ManyToOne
    @JoinColumn(name = "original_invoice_id")
    private Invoice originalInvoice;

}
