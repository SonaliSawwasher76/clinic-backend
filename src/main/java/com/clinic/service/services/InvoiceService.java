package com.clinic.service.services;

import com.clinic.dto.billing.InvoiceResponseDTO;

public interface InvoiceService {


    InvoiceResponseDTO createInvoiceForVisit(Long visitRecordId);
    // InvoiceService.java
    byte[] getInvoicePdfBytes(Long visitRecordId);



    // byte[] buildInvoicePdf(Long visitRecordId);
}
