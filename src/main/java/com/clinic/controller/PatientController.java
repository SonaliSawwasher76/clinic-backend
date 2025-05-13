package com.clinic.controller;

import com.clinic.dto.Patient.PatientRequestDTO;
import com.clinic.dto.Patient.PatientResponseDTO;
import com.clinic.service.PatientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
@Validated // This is used to apply validation annotations to method parameters in the controller
public class PatientController {

    @Autowired
    private PatientService patientService;

    @Operation(summary = "Create a new patient")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Patient created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PatientResponseDTO createPatient(@Valid @RequestBody PatientRequestDTO patientRequestDTO) {
        return patientService.createPatient(patientRequestDTO);
    }

    @Operation(summary = "Get all patients")
    @ApiResponse(responseCode = "200", description = "List of patients retrieved")
    @GetMapping
    public List<PatientResponseDTO> getAllPatients() {
        return patientService.getAllPatients();
    }

    @Operation(summary = "Get patient by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Patient found"),
            @ApiResponse(responseCode = "404", description = "Patient not found")
    })
    @GetMapping("/{id}")
    public PatientResponseDTO getPatientById(@PathVariable @Min(value = 1, message = "ID must be a positive integer") Long id) {
        return patientService.getPatientById(id);
    }

    @Operation(summary = "Update patient by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Patient updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "Patient not found")
    })
    @PutMapping("/{id}")
    public PatientResponseDTO updatePatient(@PathVariable @Min(value = 1, message = "ID must be a positive integer") Long id,
                                            @Valid @RequestBody PatientRequestDTO patientRequestDTO) {
        return patientService.updatePatient(id, patientRequestDTO);
    }

    @Operation(summary = "Delete patient by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Patient deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Patient not found")
    })


    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePatient(@PathVariable @Min(value = 1, message = "ID must be a positive integer") Long id) {
        patientService.deletePatient(id);
    }

    @GetMapping("/patient search")
    public ResponseEntity<List<PatientResponseDTO>> searchPatients(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer age,
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String contactNumber
    ) {
        List<PatientResponseDTO> results = patientService.searchPatients(name, age, gender, email, id, contactNumber);
        return ResponseEntity.ok(results);
    }
}
