package com.clinic.dto.Doctor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorRequestDTO {

    @NotBlank(message = "Specialization is required")
    private String specialization;

    @NotBlank(message = "License Number is required")
    private String licenseNumber;

    @NotNull(message = "Years of Experience is required")
    @Positive(message = "Years of Experience must be a positive number")
    private Integer yearsOfExperience;
}
