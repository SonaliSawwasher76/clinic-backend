package com.clinic.dto.Auth;

import lombok.Getter;

@Getter
public class UserLoginResponseDTO {
    private String token;
    private String role;
    private Long userId;
    private String message;

    public UserLoginResponseDTO(String token, String role, Long userId, String message) {
        this.token = token;
        this.role = role;
        this.userId = userId;
        this.message = message;
    }
}


