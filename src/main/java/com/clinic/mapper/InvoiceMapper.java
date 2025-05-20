package com.clinic.mapper;

import com.clinic.dto.billing.InvoiceResponseDTO;
import com.clinic.dto.billing.InvoiceResponseDTO.LineItemDTO;
import com.clinic.entity.Appointment.VisitRecord;
import com.clinic.entity.billing.Invoice;
import com.clinic.entity.billing.InvoiceLineItem;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public final class InvoiceMapper {

    private InvoiceMapper() {}   // utility class

    /* ---------- ENTITY BUILDERS ---------- */

    /** Build a fresh Invoice entity (with line‑items) from a completed VisitRecord. */
    public static Invoice buildInvoice(VisitRecord vr,
                                       String clinicName,
                                       LocalDateTime issueDate,String clinicAddress,String reasonForVisit) {

        // prepare line‑items from the services captured in VisitRecord
        List<InvoiceLineItem> items = vr.getServices().stream()
                .map(svc -> InvoiceLineItem.builder()
                        .serviceName(svc.getName())
                        .price(svc.getPrice())
                        .build())
                .collect(Collectors.toList());

        BigDecimal total = items.stream()
                .map(InvoiceLineItem::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // create invoice (line‑items get linked afterward)
        Invoice invoice = Invoice.builder()
                .visitRecord(vr)
                .clinicName(clinicName)
                .clinicAddress(vr.getAppointment().getDoctor().getUser().getWorkspace().getAddress())
                .patientName(vr.getAppointment().getPatient().getFirstname() + " "
                        + vr.getAppointment().getPatient().getLastname())
                .doctorName(vr.getAppointment().getDoctor().getUser().getUserProfile().getFirstName() + " "
                        + vr.getAppointment().getDoctor().getUser().getUserProfile().getLastName())
                .issueDate(issueDate)
                .paymentStatus(Invoice.PaymentStatus.PENDING)
                .totalAmount(total)
                .reasonForVisit(vr.getAppointment().getReason())
                .items(null)   // set after building (avoids circular ref in builder)
                .build();

        // back‑reference each item
        items.forEach(i -> i.setInvoice(invoice));
        invoice.setItems(items);

        return invoice;
    }

    /* ---------- DTO CONVERTERS ---------- */

    public static InvoiceResponseDTO toDTO(Invoice invoice) {
        if (invoice == null) return null;

        return InvoiceResponseDTO.builder()
                .invoiceId(invoice.getInvoiceId())
                .visitRecordId(invoice.getVisitRecord().getVisitRecordId())
                .clinicName(invoice.getClinicName())
                .clinicAddress(invoice.getClinicAddress())
                .reasonForVisit(invoice.getReasonForVisit())
                .patientName(invoice.getPatientName())
                .doctorName(invoice.getDoctorName())
                .issueDate(invoice.getIssueDate())
                .paymentStatus(invoice.getPaymentStatus().name())
                .totalAmount(invoice.getTotalAmount())
                .items(toLineDTOs(invoice.getItems()))
                .build();
    }

    private static List<LineItemDTO> toLineDTOs(List<InvoiceLineItem> items) {
        return items == null ? List.of() :
                items.stream()
                        .map(i -> LineItemDTO.builder()
                                .serviceName(i.getServiceName())
                                .price(i.getPrice())
                                .build())
                        .collect(Collectors.toList());
    }
}
