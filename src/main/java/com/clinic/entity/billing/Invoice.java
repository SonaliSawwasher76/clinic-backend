package com.clinic.entity.billing;

import com.clinic.entity.Appointment.VisitRecord;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "invoices")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long invoiceId;

    /** Snapshot link to the visit that generated this invoice */
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "visit_record_id", unique = true)
    private VisitRecord visitRecord;

    // snapshot fields for display
    private String clinicName;
    private String clinicAddress;
    private String patientName;
    private String doctorName;
    private String reasonForVisit;

    private LocalDateTime issueDate;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    private BigDecimal totalAmount;
    private String gatewayOrderId;
    private String gatewayPaymentId;


    @Lob
    @Column(name = "pdf_bytes" ,columnDefinition = "LONGBLOB")
    private byte[] pdfBytes;


    /** PDF bytes (easy for now – can switch to file‑system later) */
    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] pdfData;

    /** line items */
    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InvoiceLineItem> items;

    public enum PaymentStatus { PENDING, PAID }
}
