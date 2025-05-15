package com.clinic.dto.Patient;

import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class MedicalHistoryRequestDTO {
    private Long patientId;
    private String diseaseName;
    private LocalDate diagnosisDate;
    private String treatmentDetails;
    private String notes;
}
