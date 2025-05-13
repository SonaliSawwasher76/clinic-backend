package com.clinic.dto.Patient;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.time.Period;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientRequestDTO {

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100)
    private String firstname;

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100)
    private String lastname;

    @NotNull(message = "Date of Birth is required")
    private LocalDate dob;

    @NotBlank(message = "Gender is required")
    private String gender;

    @NotBlank(message = "Contact number is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Contact number should be a 10 digit number")
    private String contactNumber;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    private String address;

    public boolean isAgeValid() {
        if (dob == null) return false;
        int age = Period.between(dob, LocalDate.now()).getYears();
        return age >= 18;
    }
}
