package com.clinic.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientResponseDTO {

    private Long id;
    private String name;
    private int age;
    private String gender;
    private String contactNumber;
    private String email;
    private String address;
}
