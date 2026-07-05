package com.example.demo.CancelInvoice;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cancel-invoices")
@RequiredArgsConstructor
@Tag(name = "Cancel Invoice", description = "Fatura İptal ve İade İşlemleri")
public class CancelInvoiceController {

    private final CancelInvoiceService cancelInvoiceService;

    @PostMapping
    public CancelInvoiceResponseDTO createCancelInvoice(@RequestBody CancelInvoiceCreateDto request) {
        return cancelInvoiceService.createCancelRequest(request);
    }

}