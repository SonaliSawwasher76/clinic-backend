package com.clinic.controller;

import com.clinic.dto.billing.InvoiceResponseDTO;
import com.clinic.service.services.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;

    // Generate or get invoice info (JSON)
    @GetMapping("/{visitRecordId}")
    public ResponseEntity<InvoiceResponseDTO> getInvoice(@PathVariable Long visitRecordId) {
        InvoiceResponseDTO invoiceDTO = invoiceService.createInvoiceForVisit(visitRecordId);
        return ResponseEntity.ok(invoiceDTO);
    }

    @GetMapping("/visit-record/{id}/pdf")
    public ResponseEntity<byte[]> downloadInvoice(@PathVariable Long id) {
        byte[] pdf = invoiceService.getInvoicePdfBytes(id);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition
                .attachment()
                .filename("invoice-" + id + ".pdf")
                .build());

        return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
    }


}
