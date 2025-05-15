package com.clinic.dto.Patient;

import java.time.LocalDate;
import java.time.LocalDateTime;

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

public class MedicalHistoryResponseDTO {
    private Long medicalHistoryId;
    private Long patientId;
    private String diseaseName;
    private LocalDate diagnosisDate;
    private String treatmentDetails;
    private String notes;
    private String createdBy;
    private String lastModifiedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
