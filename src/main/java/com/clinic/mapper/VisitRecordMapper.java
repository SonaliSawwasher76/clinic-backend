package com.clinic.mapper;

import com.clinic.dto.Appointment.VisitRecordRequestDTO;
import com.clinic.dto.Appointment.VisitRecordResponseDTO;
import com.clinic.dto.Appointment.ServiceDTO;
import com.clinic.entity.Appointment.VisitRecord;
import com.clinic.entity.Appointment.Appointment;
import com.clinic.entity.billing.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public class VisitRecordMapper {

    // Convert entity to response DTO
    public static VisitRecordResponseDTO toDTO(VisitRecord entity) {
        if (entity == null) return null;

        return VisitRecordResponseDTO.builder()
                .visitRecordId(entity.getVisitRecordId())
                .appointmentId(entity.getAppointment().getAppointmentId())
                .patientFullName(entity.getAppointment().getPatient().getFirstname() + " " + entity.getAppointment().getPatient().getLastname())
                .doctorFullName(entity.getAppointment().getDoctor().getUser().getUserProfile().getFirstName() + " " + entity.getAppointment().getDoctor().getUser().getUserProfile().getLastName())
                .symptoms(entity.getSymptoms())
                .diagnosis(entity.getDiagnosis())
                .prescription(entity.getPrescription())
                .notes(entity.getNotes())
                .services(entity.getServices()
                        .stream()
                        .map(VisitRecordMapper::serviceToDTO)
                        .collect(Collectors.toList()))
                .totalAmount(entity.getTotalAmount())
                .visitDateTime(entity.getVisitDateTime())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    // Convert service entity to DTO
    public static ServiceDTO serviceToDTO(Service service) {
        if (service == null) return null;

        return ServiceDTO.builder()
                .serviceId(service.getServiceId())
                .name(service.getName())
                .price(service.getPrice())
                .description(service.getDescription())
                .build();
    }

    // Convert request DTO to entity
    // Note: services must be fetched from DB by serviceIds before calling this
    public static VisitRecord toEntity(VisitRecordRequestDTO dto, Appointment appointment, List<Service> services) {
        if (dto == null) return null;

        BigDecimal totalAmount = services.stream()
                .map(Service::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return VisitRecord.builder()
                .appointment(appointment)
                .symptoms(dto.getSymptoms())
                .diagnosis(dto.getDiagnosis())
                .prescription(dto.getPrescription())
                .notes(dto.getNotes())
                .services(services)
                .totalAmount(totalAmount)
                .visitDateTime(dto.getVisitDateTime())
                .createdAt(java.time.LocalDateTime.now())
                .build();
    }
}
