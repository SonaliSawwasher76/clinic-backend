package com.clinic.service.services;

import com.clinic.dto.Patient.PatientRequestDTO;
import com.clinic.dto.Patient.PatientResponseDTO;

import java.util.List;

public interface PatientService {
    PatientResponseDTO createPatient(PatientRequestDTO patientRequestDTO);

    List<PatientResponseDTO> getAllPatients();

    PatientResponseDTO getPatientById(Long id);

    PatientResponseDTO updatePatient(Long id, PatientRequestDTO patientRequestDTO);

    List<PatientResponseDTO> getPatientsByWorkspaceId(Long workspaceId);

    void deletePatient(Long id);

    List<PatientResponseDTO> searchPatients(String firstname,String lastname, String gender, String email, Long id, String contactNumber);

    List<PatientResponseDTO> searchPatientsByQuery(String query);
}
