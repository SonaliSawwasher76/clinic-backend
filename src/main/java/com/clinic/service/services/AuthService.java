package com.clinic.service.services;

import com.clinic.dto.Auth.SignupRequestWrapperDTO;
import com.clinic.dto.Auth.UserDetailsResponseDTO;
import com.clinic.dto.Auth.UserLoginRequestDTO;
import com.clinic.dto.Auth.UserLoginResponseDTO;
import com.clinic.enums.Role;

public interface AuthService {
    String signUp(SignupRequestWrapperDTO signupDto);
    UserLoginResponseDTO login(UserLoginRequestDTO dto);
    UserDetailsResponseDTO getUserDetailsById(Long userId);

}
