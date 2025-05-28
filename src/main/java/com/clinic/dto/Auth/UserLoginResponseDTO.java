package com.clinic.dto.Auth;

import lombok.Getter;

@Getter
public class UserLoginResponseDTO {
    private String token;
    private String refreshToken;
    private String role;
    private Long userId;
    private String message;
    private String firstName;
    private String workspaceName;

    public UserLoginResponseDTO(String token,String refreshToken, String role, Long userId, String message,String firstName,String workspaceName) {
        this.token = token;
        this.refreshToken = refreshToken;
        this.role = role;
        this.userId = userId;
        this.message = message;
        this.firstName = firstName;
        this.workspaceName = workspaceName;
    }
}


