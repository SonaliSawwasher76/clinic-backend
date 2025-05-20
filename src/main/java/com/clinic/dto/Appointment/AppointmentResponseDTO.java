package com.clinic.dto.Appointment;

import com.clinic.enums.AppointmentStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentResponseDTO {

    private Long appointmentId;
    private Long doctorId;
    private String doctorName;
    private Long patientId;
    private String patientName;
    private LocalDate appointmentDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String reason;


    // lifecycle
    private AppointmentStatus status;
    private String createdBy;
    private LocalDateTime createdAt;

    private String cancelledBy;
    private LocalDateTime cancelledAt;
    private String cancellationReason;

    private String rescheduledBy;
    private LocalDateTime rescheduledFrom;
    private LocalDateTime rescheduledTo;
    private String rescheduleReason;


}
