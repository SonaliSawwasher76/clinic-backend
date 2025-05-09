package com.clinic.service;

import com.clinic.dto.PatientRequestDTO;
import com.clinic.dto.PatientResponseDTO;
import com.clinic.entity.Patient;
import com.clinic.exception.InvalidInputException;
import com.clinic.exception.ResourceNotFoundException;
import com.clinic.mapper.PatientMapper;
import com.clinic.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;
    private final PatientMapper patientMapper;

    @Autowired
    public PatientServiceImpl(PatientRepository patientRepository, PatientMapper patientMapper) {
        this.patientRepository = patientRepository;
        this.patientMapper = patientMapper;
    }

    @Override
    public PatientResponseDTO createPatient(PatientRequestDTO patientRequestDTO) {
        // Validate the input data
        validatePatientInput(patientRequestDTO);

        // Convert the PatientRequestDTO to the Patient entity
        Patient patient = patientMapper.patientRequestDTOToPatient(patientRequestDTO);

        // Save the Patient entity to the database
        Patient savedPatient = patientRepository.save(patient);

        // Convert the saved Patient entity to the PatientResponseDTO and return it
        return patientMapper.patientToPatientResponseDTO(savedPatient);
    }

    @Override
    public PatientResponseDTO updatePatient(Long id, PatientRequestDTO patientRequestDTO) {
        // Validate the input data
        validatePatientInput(patientRequestDTO);

        // Check if the Patient exists
        Optional<Patient> existingPatientOptional = patientRepository.findById(id);
        if (existingPatientOptional.isPresent()) {
            // If the Patient exists, get the entity
            Patient existingPatient = existingPatientOptional.get();

            // Update only the provided fields in the PatientRequestDTO
            if (patientRequestDTO.getName() != null) {
                existingPatient.setName(patientRequestDTO.getName());
            }
            if (patientRequestDTO.getAge() != null) {
                existingPatient.setAge(patientRequestDTO.getAge());
            }
            if (patientRequestDTO.getGender() != null) {
                existingPatient.setGender(patientRequestDTO.getGender());
            }
            if (patientRequestDTO.getContactNumber() != null) {
                existingPatient.setContactNumber(patientRequestDTO.getContactNumber());
            }
            if (patientRequestDTO.getEmail() != null) {
                existingPatient.setEmail(patientRequestDTO.getEmail());
            }
            if (patientRequestDTO.getAddress() != null) {
                existingPatient.setAddress(patientRequestDTO.getAddress());
            }

            // Save the updated Patient entity
            Patient updatedPatient = patientRepository.save(existingPatient);

            // Convert and return the updated Patient entity as a DTO
            return patientMapper.patientToPatientResponseDTO(updatedPatient);
        } else {
            // If the Patient doesn't exist, throw ResourceNotFoundException
            throw new ResourceNotFoundException("Patient with ID " + id + " not found");
        }
    }

    @Override
    public PatientResponseDTO getPatientById(Long id) {
        // Fetch the Patient by id from the database
        Optional<Patient> patientOptional = patientRepository.findById(id);
        if (patientOptional.isPresent()) {
            // Convert the Patient entity to a PatientResponseDTO
            return patientMapper.patientToPatientResponseDTO(patientOptional.get());
        } else {
            // If the Patient is not found, throw ResourceNotFoundException
            throw new ResourceNotFoundException("Patient with ID " + id + " not found");
        }
    }

    @Override
    public List<PatientResponseDTO> getAllPatients() {
        // Fetch all Patients from the database
        List<Patient> patients = patientRepository.findAll();
        // Convert each Patient entity to a PatientResponseDTO and return the list
        return patients.stream()
                .map(patientMapper::patientToPatientResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void deletePatient(Long id) {
        // Check if the Patient exists
        if (patientRepository.existsById(id)) {
            // Delete the Patient from the database
            patientRepository.deleteById(id);
        } else {
            // If the Patient doesn't exist, throw ResourceNotFoundException
            throw new ResourceNotFoundException("Patient with ID " + id + " not found");
        }
    }

    private void validatePatientInput(PatientRequestDTO dto) {
        // Validate that the patient name is not null or empty
        if (dto.getName() == null || dto.getName().isEmpty()) {
            throw new InvalidInputException("Patient name cannot be null or empty");
        }

        // Validate that the patient age is greater than 0
        if (dto.getAge() == null || dto.getAge() <= 0) {
            throw new InvalidInputException("Age must be greater than 0");
        }

        // Validate that gender is not null or empty
        if (dto.getGender() == null || dto.getGender().isEmpty()) {
            throw new InvalidInputException("Gender is required");
        }

        // Add more validation logic as needed for other fields
    }
}
