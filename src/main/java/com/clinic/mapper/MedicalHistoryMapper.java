package com.clinic.mapper;


import com.clinic.dto.Patient.MedicalHistoryResponseDTO;
import com.clinic.dto.Patient.MedicalHistoryRequestDTO;
import com.clinic.entity.MedicalHistory;
import com.clinic.entity.Patient;
import org.springframework.stereotype.Component;

@Component
public class MedicalHistoryMapper {

    public MedicalHistory toEntity(MedicalHistoryRequestDTO dto, Patient patient, String username) {
        MedicalHistory mh = new MedicalHistory();
        mh.setDiseaseName(dto.getDiseaseName());
        mh.setTreatmentDetails(dto.getTreatmentDetails());
        mh.setDiagnosisDate(dto.getDiagnosisDate());
        mh.setNotes(dto.getNotes());
        mh.setPatient(patient);
        mh.setCreatedBy(username);
        mh.setLastModifiedBy(username);
        return mh;
    }

    public MedicalHistoryResponseDTO toDTO(MedicalHistory mh) {
        MedicalHistoryResponseDTO dto = new MedicalHistoryResponseDTO();
        dto.setMedicalHistoryId(mh.getMedicalHistoryId());
        dto.setDiseaseName(mh.getDiseaseName());
        dto.setTreatmentDetails(mh.getTreatmentDetails());
        dto.setDiagnosisDate(mh.getDiagnosisDate());
        dto.setNotes(mh.getNotes());
        dto.setCreatedBy(mh.getCreatedBy());
        dto.setLastModifiedBy(mh.getLastModifiedBy());
        dto.setPatientId(mh.getPatient().getId());
        dto.setCreatedAt(mh.getCreatedAt());
        dto.setUpdatedAt(mh.getUpdatedAt());
        return dto;
    }
}
