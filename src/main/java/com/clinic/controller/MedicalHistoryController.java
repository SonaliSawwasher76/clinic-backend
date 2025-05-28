package com.clinic.controller;

import com.clinic.dto.Patient.MedicalHistoryRequestDTO;
import com.clinic.dto.Patient.MedicalHistoryResponseDTO;
import com.clinic.dto.Patient.PatientResponseDTO;
import com.clinic.service.services.MedicalHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medical-history")
@RequiredArgsConstructor
public class MedicalHistoryController {

    private final MedicalHistoryService medicalHistoryService;

    @PostMapping
    @PreAuthorize("hasAnyRole('DOCTOR','ADMIN')")
    public MedicalHistoryResponseDTO addMedicalHistory(@RequestBody MedicalHistoryRequestDTO dto,
                                                       @AuthenticationPrincipal String username) {
        // Use username directly since principal is String in your filter
        return medicalHistoryService.createMedicalHistory(dto, username);
    }

    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasAnyRole('DOCTOR','ADMIN')")
    public List<MedicalHistoryResponseDTO> getByPatientId(@PathVariable Long patientId) {
        return medicalHistoryService.getMedicalHistoryByPatientId(patientId);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('DOCTOR','ADMIN')")
    public MedicalHistoryResponseDTO updateMedicalHistory(@PathVariable Long id,
                                                          @RequestBody MedicalHistoryRequestDTO dto,
                                                          @AuthenticationPrincipal String username) {
        return medicalHistoryService.updateMedicalHistory(id, dto, username);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('DOCTOR','ADMIN')")
    public void deleteMedicalHistory(@PathVariable Long id,
                                     @AuthenticationPrincipal String username) {
        medicalHistoryService.deleteMedicalHistory(id, username);
    }

    @GetMapping("/patients-with-history")
    @PreAuthorize("hasAnyRole('DOCTOR','ADMIN')")
    public List<Long> getPatientsWithMedicalHistory() {
        return medicalHistoryService.getPatientsWithMedicalHistory();
    }





}
