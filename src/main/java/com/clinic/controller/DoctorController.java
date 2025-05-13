package com.clinic.controller;

import com.clinic.dto.Doctor.DoctorRequestDTO;
import com.clinic.dto.Doctor.DoctorResponseDTO;
import com.clinic.dto.Auth.SignupRequestWrapperDTO;
import com.clinic.service.DoctorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/doctors")
public class DoctorController {

    private final DoctorService doctorService;

    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<DoctorResponseDTO> createDoctor(@RequestBody SignupRequestWrapperDTO signupRequestWrapperDTO) {
        DoctorResponseDTO doctorResponseDTO = doctorService.createDoctor(signupRequestWrapperDTO);
        return new ResponseEntity<>(doctorResponseDTO, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{doctorId}")
    public ResponseEntity<DoctorResponseDTO> updateDoctor(
            @PathVariable Long doctorId, @RequestBody DoctorRequestDTO doctorRequestDTO) {
        DoctorResponseDTO doctorResponseDTO = doctorService.updateDoctor(doctorId, doctorRequestDTO);
        return new ResponseEntity<>(doctorResponseDTO, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{doctorId}")
    public ResponseEntity<Void> deleteDoctor(@PathVariable Long doctorId) {
        doctorService.deleteDoctor(doctorId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR','RECEPTIONIST')")
    @GetMapping("/{doctorId}")
    public ResponseEntity<DoctorResponseDTO> getDoctor(@PathVariable Long doctorId) {
        DoctorResponseDTO doctorResponseDTO = doctorService.getDoctor(doctorId);
        return new ResponseEntity<>(doctorResponseDTO, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST')")
    @GetMapping
    public ResponseEntity<List<DoctorResponseDTO>> getAllDoctors() {
        List<DoctorResponseDTO> doctorResponseDTOList = doctorService.getAllDoctors();
        return new ResponseEntity<>(doctorResponseDTOList, HttpStatus.OK);
    }
}

