package com.clinic.service;

import com.clinic.dto.Doctor.DoctorRequestDTO;
import com.clinic.dto.Doctor.DoctorResponseDTO;
import com.clinic.dto.Auth.SignupRequestWrapperDTO;

import java.util.List;

public interface DoctorService {

    // Method to create a new doctor
    DoctorResponseDTO createDoctor(SignupRequestWrapperDTO signupRequestWrapperDTO);

    // Method to update an existing doctor
    DoctorResponseDTO updateDoctor(Long doctorId, DoctorRequestDTO doctorRequestDTO);

    // Method to delete a doctor
    void deleteDoctor(Long doctorId);

    // Method to get a doctor by ID
    DoctorResponseDTO getDoctor(Long doctorId);

    // Method to get all doctors
    List<DoctorResponseDTO> getAllDoctors();
}
