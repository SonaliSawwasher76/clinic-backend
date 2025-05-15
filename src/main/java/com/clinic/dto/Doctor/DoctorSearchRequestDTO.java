package com.clinic.dto.Doctor;

import lombok.Data;

@Data
public class DoctorSearchRequestDTO {
    private String firstName;
    private String lastName;
    private String contactNo;
    private String specialization;
    private String licenseNumber;
    private Integer yearsOfExperience;
    private Long doctorId;
}
