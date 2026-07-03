package com.example.demo.CancelInvoice;

import com.example.demo.Invoice.Invoice;
import com.example.demo.User.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Entity
@Getter
@Setter
public class CancelInvoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private Invoice invoice;

    private String reason;

    private LocalDateTime cancelDate;

    @ManyToOne
    private User cancelledBy;
}
