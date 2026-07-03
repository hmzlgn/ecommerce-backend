package com.example.demo.Service;

import com.example.demo.DTO.InvoiceCreateDto;
import com.example.demo.DTO.InvoiceResponseDto;
import com.example.demo.Entity.Invoice;
import com.example.demo.Entity.Order;
import com.example.demo.Repository.InvoiceRepository;
import com.example.demo.Repository.OrderRepository;
import com.example.demo.enums.InvoiceType;
import com.example.demo.enums.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InvoiceService {

}