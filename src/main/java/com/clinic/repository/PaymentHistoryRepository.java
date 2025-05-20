package com.clinic.repository;

import com.clinic.entity.billing.PaymentHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentHistoryRepository extends JpaRepository<PaymentHistory, Long> {

    /* All payments for one invoice */
    List<PaymentHistory> findByInvoice_InvoiceId(Long invoiceId);

    /* Optional helper – latest successful payment for an invoice */
    PaymentHistory findFirstByInvoice_InvoiceIdAndStatusOrderByPaidAtDesc(
            Long invoiceId,
            PaymentHistory.Status status
    );
}
