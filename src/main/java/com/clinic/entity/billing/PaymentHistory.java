/* 📄 src/main/java/com/clinic/entity/billing/PaymentHistory.java */
package com.clinic.entity.billing;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity @Table(name = "payment_history")
@Getter @Setter @Builder
@AllArgsConstructor @NoArgsConstructor
public class PaymentHistory {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentHistoryId;

    /* link back to invoice */
    @ManyToOne(fetch = FetchType.LAZY)                    // invoice_id FK
    @JoinColumn(name = "invoice_id")
    private Invoice invoice;

    /* Razorpay meta */
    private String razorpayOrderId;
    private String razorpayPaymentId;
    private String razorpaySignature;

    private long   amount;            // paise as returned by Razorpay
    private String currency;

    @Enumerated(EnumType.STRING)
    private Status status;

    private LocalDateTime paidAt;

    public enum Status { SUCCESS, FAILED }
}
