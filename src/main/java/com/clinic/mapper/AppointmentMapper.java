package com.clinic.mapper;

import com.clinic.dto.Appointment.AppointmentRequestDTO;
import com.clinic.dto.Appointment.AppointmentResponseDTO;
import com.clinic.entity.Appointment.Appointment;
import com.clinic.entity.Doctor;
import com.clinic.entity.Patient;
import lombok.experimental.UtilityClass;

@UtilityClass
public class AppointmentMapper {

    public Appointment toEntity(AppointmentRequestDTO dto) {
        // endTime will be set later in the service based on appointment duration
        return Appointment.builder()
                .appointmentDate(dto.getAppointmentDate())
                .startTime(dto.getStartTime())
                .reason(dto.getReason())

                .build();
    }

    public AppointmentResponseDTO toResponseDTO(Appointment appointment) {
        return AppointmentResponseDTO.builder()
                .appointmentId(appointment.getAppointmentId())

                // Doctor info
                .doctorId(appointment.getDoctor().getDoctorId())
                .doctorName(appointment.getDoctor().getUser().getUserProfile().getFirstName()
                        + " " + appointment.getDoctor().getUser().getUserProfile().getLastName())

                // Patient info
                .patientId(appointment.getPatient().getId())
                .patientName(appointment.getPatient().getFirstname()
                        + " " + appointment.getPatient().getLastname())

                .appointmentDate(appointment.getAppointmentDate())
                .startTime(appointment.getStartTime())
                .endTime(appointment.getEndTime())
                .reason(appointment.getReason())

                // New fields
                .status(appointment.getStatus())
                .createdAt(appointment.getCreatedAt())
                .createdBy(appointment.getCreatedBy())

                .cancelledBy(appointment.getCancelledBy())
                .cancellationReason(appointment.getCancellationReason())
                .cancelledAt(appointment.getCancelledAt())

                .rescheduledBy(appointment.getRescheduledBy())
                .rescheduledFrom(appointment.getRescheduledFrom())
                .rescheduledTo(appointment.getRescheduledTo())
                .rescheduleReason(appointment.getRescheduleReason())

                .build();
    }
}
