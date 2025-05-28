package com.clinic.service.services;

import com.clinic.dto.Auth.*;
import com.clinic.enums.Role;

public interface AuthService {
    String signUp(SignupRequestWrapperDTO signupDto);
    UserLoginResponseDTO login(UserLoginRequestDTO dto);
    UserDetailsResponseDTO getUserDetailsById(Long userId);
    RefreshTokenResponseDTO refreshAccessToken(String refreshToken);
}
