package com.clinic.controller;

import com.clinic.dto.Auth.SignupRequestWrapperDTO;
import com.clinic.dto.Auth.UserLoginRequestDTO;
import com.clinic.dto.Auth.UserLoginResponseDTO;
import com.clinic.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<String> signUp(@RequestBody @Valid SignupRequestWrapperDTO wrapperDto) {
        return ResponseEntity.ok(authService.signUp(wrapperDto));
    }


    @PostMapping("/login")
    public ResponseEntity<UserLoginResponseDTO> login(@RequestBody @Valid UserLoginRequestDTO dto) {
        return ResponseEntity.ok(authService.login(dto));
    }
}
