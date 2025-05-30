package com.clinic.dto.Patient;

import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientResponseDTO {

    private Long id;
    private String firstname;
    private String lastname;

    private String gender;
    private String contactNumber;
    private String email;
    private String address;
    private LocalDate dob;
    private Long workspaceId;

}
