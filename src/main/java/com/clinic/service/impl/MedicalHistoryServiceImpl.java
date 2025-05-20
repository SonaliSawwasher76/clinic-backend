package com.clinic.service.impl;

import com.clinic.dto.Patient.MedicalHistoryRequestDTO;
import com.clinic.dto.Patient.MedicalHistoryResponseDTO;
import com.clinic.entity.MedicalHistory;
import com.clinic.entity.Patient;
import com.clinic.exception.ResourceNotFoundException;
import com.clinic.mapper.MedicalHistoryMapper;
import com.clinic.repository.MedicalHistoryRepository;
import com.clinic.repository.PatientRepository;
import com.clinic.service.services.MedicalHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MedicalHistoryServiceImpl implements MedicalHistoryService {

    private final MedicalHistoryRepository medicalHistoryRepository;
    private final PatientRepository patientRepository;
    private final MedicalHistoryMapper mapper;
    private final AuditLogServiceImpl auditLogService;

    @Override
    public MedicalHistoryResponseDTO createMedicalHistory(MedicalHistoryRequestDTO dto, String username) {
        Patient patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id " + dto.getPatientId()));

        MedicalHistory medicalHistory = mapper.toEntity(dto, patient, username);
        MedicalHistory saved = medicalHistoryRepository.save(medicalHistory);

        auditLogService.logAction(
                "CREATE_MEDICAL_HISTORY",
                "MedicalHistory",

                "Created for patientId: " + dto.getPatientId() +"by " + username
        );
        return mapper.toDTO(saved);
    }

    @Override
    public List<MedicalHistoryResponseDTO> getMedicalHistoryByPatientId(Long patientId) {
        return medicalHistoryRepository.findByPatientId(patientId)
                .stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }


    @Override
    public MedicalHistoryResponseDTO updateMedicalHistory(Long id, MedicalHistoryRequestDTO dto, String username) {
        // 1. Fetch existing medical history by id, throw if not found
        MedicalHistory existing = medicalHistoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Medical history not found with id " + id));

        // 2. (Optional) Check if 'username' has permission to update

        // 3. Update fields from dto to entity
        existing.setDiseaseName(dto.getDiseaseName());
        existing.setTreatmentDetails(dto.getTreatmentDetails());
        existing.setDiagnosisDate(dto.getDiagnosisDate());
        existing.setNotes(dto.getNotes());
        // ... update other fields as needed

        // 4. Save updated entity
        MedicalHistory saved = medicalHistoryRepository.save(existing);
        auditLogService.logAction(
                "UPDATED_MEDICAL_HISTORY",
                "MedicalHistory",

                "Update for patientId: " + dto.getPatientId() +"by " + username
        );

        // 5. Convert to response DTO and return
        return mapper.toDTO(saved);
    }

    @Override
    public void deleteMedicalHistory(Long id, String username) {
        MedicalHistory existing = medicalHistoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Medical history not found with id " + id));

        // (Optional) check user permission before deleting
        auditLogService.logAction(
                "DELETED_MEDICAL_HISTORY",
                "MedicalHistory",

                "DELETED for MedicalHistoryId: " + id +"by " + username
        );

        medicalHistoryRepository.delete(existing);
    }

}
