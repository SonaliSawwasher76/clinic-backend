package com.clinic.dto.Auth;

import com.clinic.dto.Doctor.DoctorRequestDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SignupRequestWrapperDTO {

    @NotNull(message = "User information is required")
    @Valid
    private UserSignupRequestDTO user;

    @Valid // Only validated if role is DOCTOR in AuthServiceImpl
    private DoctorRequestDTO doctor;
}
