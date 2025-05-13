package com.clinic.service;

import com.clinic.dto.Patient.PatientRequestDTO;
import com.clinic.dto.Patient.PatientResponseDTO;

import java.util.List;

public interface PatientService {
    PatientResponseDTO createPatient(PatientRequestDTO patientRequestDTO);

    List<PatientResponseDTO> getAllPatients();

    PatientResponseDTO getPatientById(Long id);

    PatientResponseDTO updatePatient(Long id, PatientRequestDTO patientRequestDTO);

    void deletePatient(Long id);

    List<PatientResponseDTO> searchPatients(String name, Integer age, String gender, String email, Long id, String contactNumber);

}
