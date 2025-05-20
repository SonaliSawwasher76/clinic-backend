package com.clinic.service.services;

import com.clinic.dto.Appointment.AppointmentCancelDTO;
import com.clinic.dto.Appointment.AppointmentRequestDTO;
import com.clinic.dto.Appointment.AppointmentResponseDTO;
import com.clinic.dto.Appointment.AppointmentRescheduleDTO;
import com.clinic.enums.AppointmentStatus;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface AppointmentService {

    AppointmentResponseDTO bookAppointment(AppointmentRequestDTO requestDTO);

    List<AppointmentResponseDTO> getAppointmentsByDoctor(Long doctorId);

    List<AppointmentResponseDTO> getAppointmentsByPatient(Long patientId);

    void cancelAppointment(AppointmentCancelDTO cancelDTO, String cancelledByUsername);

    AppointmentResponseDTO rescheduleAppointment(AppointmentRescheduleDTO rescheduleDTO, String rescheduledByUsername);

    List<AppointmentResponseDTO> searchAppointments(
            Long doctorId,
            Long patientId,
            LocalDate appointmentDate,
            LocalTime startTime,
            LocalTime endTime,
            AppointmentStatus status,
            String createdBy,
            String cancelledBy,
            String rescheduledBy
    );
}
