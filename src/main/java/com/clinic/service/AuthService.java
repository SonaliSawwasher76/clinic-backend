package com.clinic.service;

import com.clinic.dto.Auth.SignupRequestWrapperDTO;
import com.clinic.dto.Auth.UserLoginRequestDTO;
import com.clinic.dto.Auth.UserLoginResponseDTO;

public interface AuthService {
    String signUp(SignupRequestWrapperDTO signupDto);
    UserLoginResponseDTO login(UserLoginRequestDTO dto);
}
