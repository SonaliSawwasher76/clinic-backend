package com.clinic.service.impl;

import com.clinic.dto.Appointment.VisitRecordRequestDTO;
import com.clinic.dto.Appointment.VisitRecordResponseDTO;
import com.clinic.entity.Appointment.Appointment;
import com.clinic.entity.Appointment.VisitRecord;
import com.clinic.entity.billing.Service;
import com.clinic.enums.AppointmentStatus;
import com.clinic.mapper.VisitRecordMapper;
import com.clinic.repository.AppointmentRepository;
import com.clinic.repository.ServiceRepository;
import com.clinic.repository.VisitRecordRepository;
import com.clinic.service.services.VisitRecordService;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import java.math.BigDecimal;   // add this at top of VisitRecordServiceImpl


import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
@Transactional
public class VisitRecordServiceImpl implements VisitRecordService {

    private final AppointmentRepository  appointmentRepository;
    private final ServiceRepository      serviceRepository;
    private final VisitRecordRepository  visitRecordRepository;

    /* ------------------------------------------------------------------
       COMPLETE APPOINTMENT & CREATE VISIT RECORD
       ------------------------------------------------------------------ */
    @Override
    public VisitRecordResponseDTO completeAppointment(Long appointmentId,
                                                      VisitRecordRequestDTO dto) {

        Appointment appt = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new NoSuchElementException(
                        "Appointment not found: " + appointmentId));


        if (appt.getStatus() == AppointmentStatus.COMPLETED) {
            throw new IllegalStateException("Visit already completed for this appointment.");
        }
        if (appt.getStatus() == AppointmentStatus.CANCELLED) {
            throw new IllegalStateException("Cancelled appointment can’t be completed.");
        }



        LocalDate appointmentDate = appt.getAppointmentDate();       // LocalDate
        LocalTime appointmentEndTime = appt.getEndTime(); // LocalTime

        LocalDateTime appointmentEndDateTime = LocalDateTime.of(appointmentDate, appointmentEndTime);

        if (LocalDateTime.now().isBefore(appointmentEndDateTime)) {
            throw new IllegalStateException("Cannot complete appointment before scheduled end time.");
        }



        // ── Load selected services
        List<Service> services = serviceRepository.findAllById(dto.getServiceIds());

        BigDecimal totalAmount = services.stream()          // Stream<Service>
                .map(Service::getPrice)                     // Stream<BigDecimal>
                .reduce(BigDecimal.ZERO, BigDecimal::add);  // OK


        // ── Persist VisitRecord
        VisitRecord record = VisitRecord.builder()
                .appointment(appt)
                .prescription(dto.getPrescription())
                .notes(dto.getNotes())
                .services(services)    // <-- use the List<Service> that you fetched or received
                .createdAt(LocalDateTime.now())
                .totalAmount(totalAmount)
                .visitDateTime(LocalDateTime.now())
                .diagnosis( dto.getDiagnosis())
                .symptoms( dto.getSymptoms())
                .build();

        VisitRecord saved = visitRecordRepository.save(record);

        // ── Update appointment status
        appt.setStatus(AppointmentStatus.COMPLETED);
        appointmentRepository.save(appt);

        return VisitRecordMapper.toDTO(saved);
    }

    /* ------------------------------------------------------------------
       SEARCH
       ------------------------------------------------------------------ */
    @Override
    @Transactional(readOnly = true)
    public List<VisitRecordResponseDTO> searchVisitRecords(Long visitRecordId,
                                                           Long appointmentId,
                                                           Long patientId,
                                                           Long doctorId,
                                                           LocalDateTime from,
                                                           LocalDateTime to) {

        Specification<VisitRecord> spec = (root, query, cb) -> {
            List<Predicate> p = new ArrayList<>();

            if (visitRecordId != null)
                p.add(cb.equal(root.get("visitRecordId"), visitRecordId));
            if (appointmentId != null)
                p.add(cb.equal(root.get("appointment").get("appointmentId"), appointmentId));
            if (patientId != null)
                p.add(cb.equal(root.get("appointment")
                        .get("patient").get("patientId"), patientId));
            if (doctorId != null)
                p.add(cb.equal(root.get("appointment")
                        .get("doctor").get("doctorId"), doctorId));
            if (from != null)
                p.add(cb.greaterThanOrEqualTo(root.get("visitDate"), from));
            if (to != null)
                p.add(cb.lessThanOrEqualTo(root.get("visitDate"), to));

            return cb.and(p.toArray(new Predicate[0]));
        };

        return visitRecordRepository.findAll(spec).stream()
                .map(VisitRecordMapper::toDTO)
                .toList();
    }

    public int getAmountByVisitRecordId(Long visitRecordId) {
        Optional<VisitRecord> visitRecordOpt = visitRecordRepository.findById(visitRecordId);
        if(visitRecordOpt.isEmpty()) {
            throw new RuntimeException("VisitRecord not found for id: " + visitRecordId);
        }
        VisitRecord visitRecord = visitRecordOpt.get();

        double amountInRs = visitRecord.getTotalAmount().doubleValue();
        return (int)(amountInRs * 100);  // Razorpay amount in paise (int)
    }
}
