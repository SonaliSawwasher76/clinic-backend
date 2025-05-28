package com.clinic.service.impl;

import com.clinic.dto.Auth.*;
import com.clinic.entity.Doctor;
import com.clinic.entity.Workspace;
import com.clinic.entity.user.User;
import com.clinic.entity.user.UserProfile;
import com.clinic.enums.Role;
import com.clinic.repository.DoctorRepository;
import com.clinic.repository.UserRepository;
import com.clinic.repository.WorkspaceRepository;
import com.clinic.service.services.AuthService;
import com.clinic.service.services.AuditLogService;
import com.clinic.util.JwtUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final DoctorRepository doctorRepository;
    private final AuditLogService auditLogService;
    private final JwtUtil jwtUtil;
    private final WorkspaceRepository workspaceRepository;




    @Override
    @Transactional
    public String signUp(SignupRequestWrapperDTO dto) {
        // Check if a user already exists
        if (userRepository.findByEmail(dto.getUser().getEmail()).isPresent()) {
            return "User already exists";
        }
        if (!dto.getUser().isAgeValid()) {
            throw new IllegalArgumentException("Age must be greater than 18");
        }
        Long workspaceId = dto.getUser().getWorkspaceId();
        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Workspace ID: " + workspaceId));



        // Create a User entity
        User user = new User();
        user.setEmail(dto.getUser().getEmail());
        user.setPassword(passwordEncoder.encode(dto.getUser().getPassword()));
        user.setRole(dto.getUser().getRole());
        user.setWorkspace(workspace);

        // Create a UserProfile entity
        UserProfile profile = new UserProfile();
        profile.setFirstName(dto.getUser().getFirstName());
        profile.setLastName(dto.getUser().getLastName());
        profile.setDob(dto.getUser().getDob());
        profile.setContactNo(dto.getUser().getContactNo());
        profile.setGender(dto.getUser().getGender());
        profile.setAddress(dto.getUser().getAddress());
       // profile.setUser(user); // Set UserProfile's user field
        user.setUserProfile(profile);

        Doctor doctor1 = new Doctor();
        doctor1.setYearsOfExperience(1);

        // Save the user and profile
        userRepository.save(user);
        auditLogService.logAction("User Registered", "Auth", "New user registered: " + dto.getUser().getEmail());

        // If the role is DOCTOR, validate and save doctor data
        if (user.getRole() == Role.DOCTOR) {
            Doctor doctor = new Doctor();
            doctor.setUser(user);
            doctor.setSpecialization(dto.getDoctor().getSpecialization());
            doctor.setLicenseNumber(dto.getDoctor().getLicenseNumber());
            doctor.setYearsOfExperience(dto.getDoctor().getYearsOfExperience());

            doctorRepository.save(doctor);
            auditLogService.logAction("Doctor Data Registered", "Auth", "Doctor data saved for: " + dto.getUser().getEmail());
        }

        return "User registered successfully";
    }

    @Override
    public UserLoginResponseDTO login(UserLoginRequestDTO dto) {
        // Find a user by email
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if the password matches
        if (passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            // Generate token here
           // String token = jwtUtil.generateToken(user.getEmail());
            String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
            String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());

            // Return successful login response
            return new UserLoginResponseDTO(token,refreshToken, user.getRole().name(), user.getUserId(), "Login successful",user.getUserProfile().getFirstName(),user.getWorkspace().getName());
        } else {
            throw new RuntimeException("Invalid credentials");
        }
    }

    public UserDetailsResponseDTO getUserDetailsById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserDetailsResponseDTO response = new UserDetailsResponseDTO();

        // Common user info
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());
        response.setWorkspaceId(user.getWorkspace().getWorkspaceId());
        response.setUserId(user.getUserId());

        // User profile info
        UserProfile profile = user.getUserProfile();
        response.setFirstName(profile.getFirstName());
        response.setLastName(profile.getLastName());
        response.setDob(profile.getDob());
        response.setContactNo(profile.getContactNo());
        response.setGender(profile.getGender());
        response.setAddress(profile.getAddress());

        // If user is doctor, add doctor details
        if(user.getRole() == Role.DOCTOR) {
            Optional<Doctor> doctorOpt = doctorRepository.findByUser(user);
            if (doctorOpt.isPresent()) {
                Doctor doctor = doctorOpt.get();
                response.setSpecialization(doctor.getSpecialization());
                response.setLicenseNumber(doctor.getLicenseNumber());
                response.setYearsOfExperience(doctor.getYearsOfExperience());

            }

        }

        return response;
    }

    @Override
    public RefreshTokenResponseDTO refreshAccessToken(String refreshToken) {
        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new RuntimeException("Refresh token is missing");
        }

        String username;
        try {
            username = jwtUtil.extractUsername(refreshToken);
        } catch (Exception e) {
            throw new RuntimeException("Invalid refresh token");
        }

        if (!jwtUtil.isRefreshTokenValid(refreshToken, username)) {
            throw new RuntimeException("Refresh token is invalid or expired");
        }

        // You can fetch the user role from DB or from token claims if you store the role in a refresh token
        String role = "USER"; // or fetch the actual role from DB

        String newAccessToken = jwtUtil.generateToken(username, role);
        String newRefreshToken = jwtUtil.generateRefreshToken(username);

        return new RefreshTokenResponseDTO(newAccessToken, newRefreshToken);
    }


}
