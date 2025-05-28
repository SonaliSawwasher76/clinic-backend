package com.clinic.controller;

import com.clinic.dto.Auth.*;
import com.clinic.entity.user.User;
import com.clinic.repository.UserRepository;
import com.clinic.service.services.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;

//    @PostMapping("/signup")
//    public ResponseEntity<String> signUp(@RequestBody @Valid SignupRequestWrapperDTO wrapperDto) {
//        return ResponseEntity.ok(authService.signUp(wrapperDto));
//    }

    @PostMapping("/signup")
    public ResponseEntity<Map<String, String>> signUp(@RequestBody @Valid SignupRequestWrapperDTO wrapperDto) {
        String message = authService.signUp(wrapperDto);
        Map<String, String> response = new HashMap<>();
        response.put("message", message);
        return ResponseEntity.ok(response);
    }



    @PostMapping("/login")
    public ResponseEntity<UserLoginResponseDTO> login(@RequestBody @Valid UserLoginRequestDTO dto) {
        return ResponseEntity.ok(authService.login(dto));
    }

    @GetMapping("/users/{userId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR') or hasRole('RECEPTIONIST')")
    public ResponseEntity<UserDetailsResponseDTO> getUserDetails(@PathVariable Long userId) {
        UserDetailsResponseDTO userDetails = authService.getUserDetailsById(userId);
        return ResponseEntity.ok(userDetails);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshAccessToken(@RequestBody RefreshTokenRequestDTO request) {
        try {
            RefreshTokenResponseDTO response = authService.refreshAccessToken(request.getRefreshToken());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }



}
