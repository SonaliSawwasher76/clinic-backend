package com.clinic.service.impl;

import com.clinic.dto.Auth.SignupRequestWrapperDTO;
import com.clinic.dto.Auth.UserLoginRequestDTO;
import com.clinic.dto.Auth.UserLoginResponseDTO;
import com.clinic.entity.Doctor;
import com.clinic.entity.user.User;
import com.clinic.entity.user.UserProfile;
import com.clinic.enums.Role;
import com.clinic.repository.DoctorRepository;
import com.clinic.repository.UserRepository;
import com.clinic.service.AuthService;
import com.clinic.service.AuditLogService;
import com.clinic.util.JwtUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final DoctorRepository doctorRepository;
    private final AuditLogService auditLogService;
    private final JwtUtil jwtUtil;



    @Override
    @Transactional
    public String signUp(SignupRequestWrapperDTO dto) {
        // Check if a user already exists
        if (userRepository.findByEmail(dto.getUser().getEmail()).isPresent()) {
            return "User already exists";
        }

        // Create a User entity
        User user = new User();
        user.setEmail(dto.getUser().getEmail());
        user.setPassword(passwordEncoder.encode(dto.getUser().getPassword()));
        user.setRole(dto.getUser().getRole());

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

            // Return successful login response
            return new UserLoginResponseDTO(token, user.getRole().name(), user.getUserId(), "Login successful");
        } else {
            throw new RuntimeException("Invalid credentials");
        }
    }
}
