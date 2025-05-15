package com.clinic.dto.Doctor;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class DoctorResponseDTO {
    private Long doctorId;
    private String firstName;
    private String lastName;
    private String email;
    private String gender;
    private String contactNo;
    private String specialization;
    private String licenseNumber;
    private Integer yearsOfExperience;
    private String address;
}
