package com.clinic.dto.Appointment;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data


public class AppointmentCancelDTO {
    @NotNull
    private Long appointmentId;
    @NotBlank
    private String cancellationReason;
}

