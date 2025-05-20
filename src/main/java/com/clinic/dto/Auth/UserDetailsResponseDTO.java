package com.clinic.dto.Auth;



import com.clinic.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailsResponseDTO {

    private Long userId;
    private String email;
    private Role role;
    private Long workspaceId;

    // From UserProfile
    private String firstName;
    private String lastName;
    private LocalDate dob;
    private String contactNo;
    private String gender;
    private String address;

    // From Doctor (nullable if not doctor)
    private String specialization;
    private String licenseNumber;
    private Integer yearsOfExperience;
}
