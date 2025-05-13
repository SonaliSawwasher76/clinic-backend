package com.clinic.mapper;

import com.clinic.dto.Patient.PatientRequestDTO;
import com.clinic.dto.Patient.PatientResponseDTO;
import com.clinic.entity.Patient;
import org.springframework.stereotype.Component;

@Component
public class PatientMapper {

    // Convert Patient entity to PatientResponseDTO
    public PatientResponseDTO patientToPatientResponseDTO(Patient patient) {
        if (patient == null) {
            return null;
        }
        return PatientResponseDTO.builder()
                .id(patient.getId())
                .name(patient.getName())
                .age(patient.getAge())
                .gender(patient.getGender())
                .contactNumber(patient.getContactNumber())
                .address(patient.getAddress())
                .email(patient.getEmail())  // Include email if needed
                .build();
    }

    // Convert PatientRequestDTO to Patient entity
    public Patient patientRequestDTOToPatient(PatientRequestDTO patientRequestDTO) {
        if (patientRequestDTO == null) {
            return null;
        }
        return Patient.builder()
                .name(patientRequestDTO.getName())
                .age(patientRequestDTO.getAge())
                .gender(patientRequestDTO.getGender())
                .contactNumber(patientRequestDTO.getContactNumber())
                .address(patientRequestDTO.getAddress())
                .email(patientRequestDTO.getEmail())  // Include email if needed
                .build();
    }
}
