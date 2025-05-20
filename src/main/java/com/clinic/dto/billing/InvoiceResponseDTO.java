package com.clinic.dto.billing;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class InvoiceResponseDTO {
    private Long invoiceId;
    private Long visitRecordId;
    private String clinicName;
    private String patientName;
    private String doctorName;
    private String clinicAddress;
    private String reasonForVisit;
    private LocalDateTime issueDate;
    private String paymentStatus;
    private BigDecimal totalAmount;
    private List<LineItemDTO> items;   // nested DTO
    private byte[] pdf;

    @Data @Builder
    public static class LineItemDTO {
        private String serviceName;
        private BigDecimal price;
    }
}
