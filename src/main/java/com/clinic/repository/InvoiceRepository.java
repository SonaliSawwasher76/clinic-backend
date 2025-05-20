package com.clinic.repository;

import com.clinic.entity.billing.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    /** Optional helpers if you ever need them */

    // Find an invoice that belongs to a visit‑record
    Optional<Invoice> findByVisitRecord_VisitRecordId(Long visitRecordId);

    // Find all invoices for a patient
    // List<Invoice> findByVisitRecord_Appointment_Patient_PatientId(Long patientId);
}
