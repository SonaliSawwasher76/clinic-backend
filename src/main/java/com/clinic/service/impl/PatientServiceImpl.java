package com.clinic.service.impl;

import com.clinic.dto.Patient.PatientRequestDTO;
import com.clinic.dto.Patient.PatientResponseDTO;
import com.clinic.entity.Patient;
import com.clinic.exception.InvalidInputException;
import com.clinic.exception.ResourceNotFoundException;
import com.clinic.mapper.PatientMapper;
import com.clinic.repository.PatientRepository;
import com.clinic.service.AuditLogService;  // Import the AuditLogService
import com.clinic.service.PatientService;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;
    private final PatientMapper patientMapper;
    private final AuditLogService auditLogService;  // AuditLogService field

    @Autowired
    public PatientServiceImpl(PatientRepository patientRepository,
                              PatientMapper patientMapper,
                              AuditLogService auditLogService) {
        this.patientRepository = patientRepository;
        this.patientMapper = patientMapper;
        this.auditLogService = auditLogService;  // Initialize the AuditLogService
    }

    @Override
    public PatientResponseDTO createPatient(PatientRequestDTO patientRequestDTO) {
        if (patientRepository.findByEmail(patientRequestDTO.getEmail()).isPresent()) {
            throw new RuntimeException("Patient with this email already exists.");
        }

        // Validate the input data
        validatePatientInput(patientRequestDTO);

        // Convert the PatientRequestDTO to the Patient entity
        Patient patient = patientMapper.patientRequestDTOToPatient(patientRequestDTO);

        // Save the Patient entity to the database
        Patient savedPatient = patientRepository.save(patient);

        // Log the creation action
        auditLogService.logAction(
                "CREATE_PATIENT",
                "PatientModule",
                "Patient created with ID: " + savedPatient.getId()
        );

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

            // Log the update action
            auditLogService.logAction(
                    "UPDATE_PATIENT",
                    "PatientModule",
                    "Patient updated with ID: " + updatedPatient.getId()
            );

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

            // Log the deletion action
            auditLogService.logAction(
                    "DELETE_PATIENT",
                    "PatientModule",
                    "Patient deleted with ID: " + id
            );
        } else {
            // If the Patient doesn't exist, throw ResourceNotFoundException
            throw new ResourceNotFoundException("Patient with ID " + id + " not found");
        }
    }

    @Override
    public List<PatientResponseDTO> searchPatients(String name, Integer age, String gender, String email, Long id, String contactNumber) {
        Specification<Patient> spec = (root, query, cb) -> {
            Predicate predicate = cb.conjunction();

            if (name != null && !name.isEmpty()) {
                predicate = cb.and(predicate, cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
            }
            if (age != null) {
                predicate = cb.and(predicate, cb.equal(root.get("age"), age));
            }
            if (gender != null && !gender.isEmpty()) {
                predicate = cb.and(predicate, cb.equal(cb.lower(root.get("gender")), gender.toLowerCase()));
            }
            if (email != null && !email.isEmpty()) {
                predicate = cb.and(predicate, cb.like(cb.lower(root.get("email")), "%" + email.toLowerCase() + "%"));
            }
            if (id != null) {
                predicate = cb.and(predicate, cb.equal(root.get("id"), id));
            }
            if (contactNumber != null && !contactNumber.isEmpty()) {
                predicate = cb.and(predicate, cb.equal(root.get("contactNumber"), contactNumber));
            }

            return predicate;
        };

        return patientRepository.findAll(spec).stream()
                .map(patientMapper::patientToPatientResponseDTO)
                .collect(Collectors.toList());
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


    }
}
