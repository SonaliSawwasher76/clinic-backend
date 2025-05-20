package com.clinic.service.impl;

import com.clinic.dto.billing.InvoiceResponseDTO;
import com.clinic.entity.Appointment.VisitRecord;
import com.clinic.entity.billing.Invoice;
import com.clinic.enums.AppointmentStatus;
import com.clinic.mapper.InvoiceMapper;
import com.clinic.repository.InvoiceRepository;
import com.clinic.repository.VisitRecordRepository;
import com.clinic.service.services.InvoiceService;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional
public class InvoiceServiceImpl implements InvoiceService {

    private final VisitRecordRepository visitRecordRepository;
    private final InvoiceRepository     invoiceRepository;

    /** Main entry‑point used by controller */
    @Override
    public InvoiceResponseDTO createInvoiceForVisit(Long visitRecordId) {

        // 1️⃣  Look‑up visit record
        VisitRecord visitRecord = visitRecordRepository.findById(visitRecordId)
                .orElseThrow(() ->
                        new NoSuchElementException("Visit record not found: " + visitRecordId));

        // 2️⃣  Must be completed
        if (visitRecord.getAppointment().getStatus() != AppointmentStatus.COMPLETED) {
            throw new IllegalStateException(
                    "Invoice can only be generated once the appointment is COMPLETED");
        }

        // 3️⃣  Fetch or build invoice
        Invoice invoice = invoiceRepository
                .findByVisitRecord_VisitRecordId(visitRecordId)
                .orElseGet(() -> {
                    Invoice newInvoice = Invoice.builder()
                            .visitRecord(visitRecord)
                            .issueDate(LocalDateTime.now())
                            .totalAmount(visitRecord.getTotalAmount())
                            .patientName(
                                    visitRecord.getAppointment().getPatient().getFirstname() + " " +
                                            visitRecord.getAppointment().getPatient().getLastname())
                            .doctorName(
                                    visitRecord.getAppointment().getDoctor().getUser()
                                            .getUserProfile().getFirstName() + " " +
                                            visitRecord.getAppointment().getDoctor().getUser()
                                                    .getUserProfile().getLastName())
                            .clinicName(
                                    visitRecord.getAppointment()
                                            .getDoctor()
                                            .getUser().getWorkspace().getName()          // whatever the field is on Doctor
                                            )
                            .clinicAddress(visitRecord.getAppointment().getDoctor().getUser().getWorkspace().getAddress())
                            .reasonForVisit(visitRecord.getAppointment().getReason())
                            .paymentStatus(Invoice.PaymentStatus.PENDING)
                            .build();
                    return invoiceRepository.save(newInvoice);
                });

        // 4️⃣  Produce PDF if not yet stored
        if (invoice.getPdfBytes() == null) {
            byte[] pdf = buildInvoicePdf(invoice);
            invoice.setPdfBytes(pdf);
            invoiceRepository.save(invoice);               // persist PDF
        }

        // 5️⃣  Map to DTO & include PDF bytes
        InvoiceResponseDTO dto = InvoiceMapper.toDTO(invoice);
        dto.setPdf(invoice.getPdfBytes());                 // field exists in DTO

        return dto;
    }

    /** Creates a simple PDF using iText 7 and returns raw bytes */
    private byte[] buildInvoicePdf(Invoice invoice) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer         = new PdfWriter(baos);
        PdfDocument pdfDoc       = new PdfDocument(writer);
        Document doc             = new Document(pdfDoc);

        // ---- Header ----
        doc.add(new Paragraph("Clinic : " + invoice.getClinicName()).setBold().setFontSize(18));
        doc.add(new Paragraph("Add:-" + invoice.getClinicAddress()).setBold().setFontSize(11));

        doc.add(new Paragraph("INVOICE").setBold().setFontSize(14));
        //doc.add(new Paragraph("INVOICE No:").setBold().setFontSize(14));
        String invoiceNumber = "INV-" + LocalDateTime.now().getYear() + "-" + invoice.getInvoiceId();
        doc.add(new Paragraph("Invoice No: " + invoiceNumber).setFontSize(12));

        doc.add(new Paragraph("Invoice Date: " + invoice.getIssueDate()));
        doc.add(new Paragraph("\n"));

        // ---- Patient / Doctor ----
        doc.add(new Paragraph("Patient: " + invoice.getPatientName()));
        doc.add(new Paragraph("Doctor : " + invoice.getDoctorName()));
        doc.add(new Paragraph("Reason : " + invoice.getReasonForVisit()));
        doc.add(new Paragraph("\n"));

        // ---- Amount ----
        doc.add(new Paragraph("Amount Payable: ₹ " + invoice.getTotalAmount()));
        doc.add(new Paragraph("\n"));

        // ---- Footer ----
        doc.add(new Paragraph("Payment Status: " + invoice.getPaymentStatus()));
        doc.add(new Paragraph("Thank you for visiting!").setItalic());

        doc.close();   // closes writer, fills baos
        return baos.toByteArray();
    }

    @Override
    public byte[] getInvoicePdfBytes(Long visitRecordId) {
        // createInvoiceForVisit() already generates & caches the PDF
        return createInvoiceForVisit(visitRecordId).getPdf();
    }
}
