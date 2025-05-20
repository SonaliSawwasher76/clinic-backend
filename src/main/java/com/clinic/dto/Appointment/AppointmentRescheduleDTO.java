package com.clinic.dto.Appointment;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data

public class AppointmentRescheduleDTO {
    @NotNull
    private Long appointmentId;

    @NotNull @FutureOrPresent
    private LocalDate newDate;

    @NotNull
    private LocalTime newStartTime;

    @NotBlank
    private String rescheduleReason;
}

