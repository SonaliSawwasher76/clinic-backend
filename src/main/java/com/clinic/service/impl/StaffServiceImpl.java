package com.clinic.service.impl;

import com.clinic.dto.Auth.UserDetailsResponseDTO;
import com.clinic.entity.Doctor;
import com.clinic.entity.user.User;
import com.clinic.entity.user.UserProfile;
import com.clinic.enums.Role;
import com.clinic.repository.DoctorRepository;
import com.clinic.repository.UserRepository;
import com.clinic.service.services.StaffService;
import com.clinic.specification.UserSpecification;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StaffServiceImpl implements StaffService {

    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;

    @Override
    public Page<UserDetailsResponseDTO> getStaffByFilters(Long workspaceId, String role, String searchText, Pageable pageable) {
        Specification<User> spec = Specification
                .where(UserSpecification.byWorkspaceId(workspaceId))
                .and(UserSpecification.byRole(role))
                .and(UserSpecification.searchByText(searchText));

        System.out.println("SearchText: " + searchText);

        Page<User> usersPage = userRepository.findAll(spec, pageable);

        // Map User entities to UserDetailsResponseDTO
        return usersPage.map(this::mapToDTO);
    }

    private UserDetailsResponseDTO mapToDTO(User user) {
        var profile = user.getUserProfile();

        // Doctor fields (nullable)
        String specialization = null;
        String licenseNumber = null;
        Integer yearsOfExperience = null;

        if ("DOCTOR".equalsIgnoreCase(user.getRole().name())) {
            if (user.getUserId() != null) {
                var doctor = user.getUserId();

            }

        }


        return UserDetailsResponseDTO.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .role(user.getRole())
                .workspaceId(user.getWorkspace().getWorkspaceId())
                .firstName(profile != null ? profile.getFirstName() : null)
                .lastName(profile != null ? profile.getLastName() : null)
                .dob(profile != null ? profile.getDob() : null)
                .contactNo(profile != null ? profile.getContactNo() : null)
                .gender(profile != null ? profile.getGender() : null)
                .address(profile != null ? profile.getAddress() : null)
                .specialization(specialization)
                .licenseNumber(licenseNumber)
                .yearsOfExperience(yearsOfExperience)
                .build();
    }


    @Override
    @Transactional
    public UserDetailsResponseDTO updateStaff(Long userId, UserDetailsResponseDTO dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // Update user fields
        user.setEmail(dto.getEmail());
        user.setRole(dto.getRole());

        // Update user profile via user entity (thanks to cascade)
        UserProfile profile = user.getUserProfile();
        if (profile == null) {
            profile = new UserProfile();
            user.setUserProfile(profile);
        }

        profile.setFirstName(dto.getFirstName());
        profile.setLastName(dto.getLastName());
        profile.setDob(dto.getDob());
        profile.setContactNo(dto.getContactNo());
        profile.setGender(dto.getGender());
        profile.setAddress(dto.getAddress());

        // Update doctor if a user is a doctor
        if (dto.getRole() == Role.DOCTOR) {
            Doctor doctor = doctorRepository.findByUserUserId(userId)
                    .orElseGet(() -> Doctor.builder().user(user).build());

            doctor.setSpecialization(dto.getSpecialization());
            doctor.setLicenseNumber(dto.getLicenseNumber());
            doctor.setYearsOfExperience(dto.getYearsOfExperience());

            doctorRepository.save(doctor);
        } else {
            // Remove doctor record if the role is no longer DOCTOR
            doctorRepository.findByUserUserId(userId)
                    .ifPresent(doctorRepository::delete);
        }

        userRepository.save(user); // This will cascade the profile save

        return dto;
    }

    @Override
    @Transactional
    public void deleteStaff(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // Delete doctor entry if exists
        doctorRepository.findByUserUserId(userId)
                .ifPresent(doctorRepository::delete);

        // Delete user (cascade removes profile)
        userRepository.delete(user);
    }

}
