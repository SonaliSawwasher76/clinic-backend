package com.clinic.controller;

import com.clinic.dto.Doctor.DoctorResponseDTO;
import com.clinic.dto.Auth.SignupRequestWrapperDTO;
import com.clinic.service.services.DoctorService;
import jakarta.validation.Valid;
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

    @PutMapping("/{doctorId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DoctorResponseDTO> updateDoctor(
            @PathVariable Long doctorId,
            @Valid @RequestBody SignupRequestWrapperDTO request) {

        DoctorResponseDTO result = doctorService.updateDoctor(doctorId, request);
        return ResponseEntity.ok(result);
    }






    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{doctorId}")
    public ResponseEntity<String> deleteDoctor(@PathVariable Long doctorId) {
        doctorService.deleteDoctor(doctorId);
        return ResponseEntity.ok("Doctor deleted successfully.");
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

//    @GetMapping("/search")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<List<DoctorResponseDTO>> searchDoctors(@ModelAttribute DoctorSearchRequestDTO request) {
//        List<DoctorResponseDTO> results = doctorService.searchDoctors(request);
//        return ResponseEntity.ok(results);
//    }

    @GetMapping("/search")
    public ResponseEntity<List<DoctorResponseDTO>> searchDoctors(
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) Long doctorId,
            @RequestParam(required = false) String specialization,
            @RequestParam(required = false) String licenseNumber,
            @RequestParam(required = false) Integer yearsOfExperience,
            @RequestParam(required = false) String contactNo
    ) {
        System.out.println("Received search parameters:");
        System.out.println("firstName = " + firstName);
        System.out.println("lastName = " + lastName);
        System.out.println("doctorId = " + doctorId);
        System.out.println("specialization = " + specialization);
        System.out.println("licenseNumber = " + licenseNumber);
        System.out.println("yearsOfExperience = " + yearsOfExperience);
        System.out.println("contactNo = " + contactNo);
        List<DoctorResponseDTO> results = doctorService.searchDoctors(
                firstName, lastName, doctorId, specialization, licenseNumber, yearsOfExperience, contactNo
        );
        return ResponseEntity.ok(results);
    }


}

