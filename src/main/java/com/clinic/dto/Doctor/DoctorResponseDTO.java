package com.clinic.dto.Doctor;

import lombok.Data;

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
}
