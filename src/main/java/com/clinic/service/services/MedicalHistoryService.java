package com.clinic.service.services;

import com.clinic.dto.Patient.MedicalHistoryRequestDTO;
import com.clinic.dto.Patient.MedicalHistoryResponseDTO;

import java.util.List;


public interface MedicalHistoryService {
    MedicalHistoryResponseDTO createMedicalHistory(MedicalHistoryRequestDTO dto, String username);
    List<MedicalHistoryResponseDTO> getMedicalHistoryByPatientId(Long patientId);
    MedicalHistoryResponseDTO updateMedicalHistory(Long id, MedicalHistoryRequestDTO dto, String username);

    void deleteMedicalHistory(Long id, String username);
    List<Long> getPatientsWithMedicalHistory();


}


