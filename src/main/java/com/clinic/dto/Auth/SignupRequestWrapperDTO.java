package com.clinic.dto.Auth;

import com.clinic.dto.Doctor.DoctorRequestDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor    // This generates a public constructor with (UserSignupRequestDTO user, DoctorRequestDTO doctor)
@NoArgsConstructor
@Builder
@Data
public class SignupRequestWrapperDTO {

    @NotNull(message = "User information is required")
    @Valid
    private UserSignupRequestDTO user;

    @Valid // Only validated if role is DOCTOR in AuthServiceImpl
    private DoctorRequestDTO doctor;
}
