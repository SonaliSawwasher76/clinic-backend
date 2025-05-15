package com.clinic.service.impl;

import com.clinic.dto.Patient.PatientRequestDTO;
import com.clinic.dto.Patient.PatientResponseDTO;
import com.clinic.entity.Patient;
import com.clinic.exception.ResourceNotFoundException;
import com.clinic.mapper.PatientMapper;
import com.clinic.repository.PatientRepository;
import com.clinic.service.AuditLogService;  // Ensure this import is included

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PatientServiceImplTest {

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private PatientMapper patientMapper;

    @Mock
    private AuditLogService auditLogService;  // Mock the AuditLogService

    @InjectMocks
    private PatientServiceImpl patientService;

    private DateTimeFormatter formatter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    }

    @Test
    void testGetPatientById_Success() {
        Patient patient = Patient.builder()
                .id(1L)
                .firstname("John")
                .lastname("Doe")
                .dob(LocalDate.parse("25-03-2007", formatter))
                .gender("Male")
                .contactNumber("1234567890")
                .address("123 Main St")
                .email("john@example.com")
                .build();

        PatientResponseDTO responseDTO = PatientResponseDTO.builder()
                .id(1L)
                .firstname("John")
                .lastname("Doe")
                .dob(LocalDate.parse("25-03-2007", formatter))
                .gender("Male")
                .contactNumber("1234567890")
                .address("123 Main St")
                .email("john@example.com")
                .build();

        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(patientMapper.patientToPatientResponseDTO(patient)).thenReturn(responseDTO);

        PatientResponseDTO result = patientService.getPatientById(1L);

        assertNotNull(result);
        assertEquals("John", result.getFirstname());
        verify(patientRepository, times(1)).findById(1L);
    }

    @Test
    void testGetPatientById_NotFound() {
        when(patientRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            patientService.getPatientById(1L);
        });

        verify(patientRepository, times(1)).findById(1L);
    }

    @Test
    void testCreatePatient_Success() {
        PatientRequestDTO requestDTO = PatientRequestDTO.builder()
                .firstname("Jane")
                .lastname("Doe")
                .dob(LocalDate.parse("25-03-2007", formatter))
                .gender("Female")
                .contactNumber("9876543210")
                .address("456 Park Ave")
                .email("jane@example.com")
                .build();

        Patient patient = Patient.builder()
                .id(2L)
                .firstname("Jane")
                .lastname("Doe")
                .dob(LocalDate.parse("25-03-2007", formatter))
                .gender("Female")
                .contactNumber("9876543210")
                .address("456 Park Ave")
                .email("jane@example.com")
                .build();

        PatientResponseDTO responseDTO = PatientResponseDTO.builder()
                .id(2L)
                .firstname("Jane")
                .lastname("Doe")
                .dob(LocalDate.parse("25-03-2007", formatter))
                .gender("Female")
                .contactNumber("9876543210")
                .address("456 Park Ave")
                .email("jane@example.com")
                .build();

        when(patientMapper.patientRequestDTOToPatient(requestDTO)).thenReturn(patient);
        when(patientRepository.save(patient)).thenReturn(patient);
        when(patientMapper.patientToPatientResponseDTO(patient)).thenReturn(responseDTO);

        // Verify that AuditLogService's logAction method is called
        doNothing().when(auditLogService).logAction(anyString(), anyString(), anyString());

        PatientResponseDTO result = patientService.createPatient(requestDTO);

        assertNotNull(result);
        assertEquals("Jane", result.getFirstname());
        verify(patientRepository, times(1)).save(patient);
        verify(auditLogService, times(1)).logAction(anyString(), anyString(), anyString());  // Verify audit log
    }

    @Test
    void testUpdatePatient_Success() {
        PatientRequestDTO requestDTO = PatientRequestDTO.builder()
                .firstname("Jane")
                .lastname("Doe")
                .dob(LocalDate.parse("25-03-2007", formatter))
                .gender("Female")
                .contactNumber("9876543210")
                .address("456 Park Ave")
                .email("jane.updated@example.com")  // Updated email
                .build();

        Patient patient = Patient.builder()
                .id(2L)
                .firstname("Jane")
                .lastname("Doe")
                .dob(LocalDate.parse("25-03-2007", formatter))
                .gender("Female")
                .contactNumber("9876543210")
                .address("456 Park Ave")
                .email("jane.updated@example.com")  // Updated email
                .build();

        PatientResponseDTO responseDTO = PatientResponseDTO.builder()
                .id(2L)
                .firstname("Jane")
                .lastname("Doe")
                .dob(LocalDate.parse("25-03-2007", formatter))
                .gender("Female")
                .contactNumber("9876543210")
                .address("456 Park Ave")
                .email("jane.updated@example.com")  // Updated email
                .build();

        when(patientRepository.findById(2L)).thenReturn(Optional.of(patient));
        when(patientMapper.patientRequestDTOToPatient(requestDTO)).thenReturn(patient);
        when(patientRepository.save(patient)).thenReturn(patient);
        when(patientMapper.patientToPatientResponseDTO(patient)).thenReturn(responseDTO);

        PatientResponseDTO result = patientService.updatePatient(2L, requestDTO);

        assertNotNull(result);
        assertEquals("Jane", result.getFirstname());
        assertEquals("jane.updated@example.com", result.getEmail());  // Assert updated email
        verify(patientRepository, times(1)).save(patient);
    }

    @Test
    void testUpdatePatient_NotFound() {
        PatientRequestDTO requestDTO = PatientRequestDTO.builder()
                .firstname("Jane")
                .lastname("Doe")
                .dob(LocalDate.parse("25-03-2007", formatter))
                .gender("Female")
                .contactNumber("9876543210")
                .address("456 Park Ave")
                .email("jane.updated@example.com")
                .build();

        when(patientRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            patientService.updatePatient(2L, requestDTO);
        });

        verify(patientRepository, times(1)).findById(2L);
    }

    @Test
    void testDeletePatient_Success() {
        Long patientId = 2L;
        Patient patient = new Patient(); // assuming entity
        patient.setId(patientId);

        when(patientRepository.existsById(patientId)).thenReturn(true);
       // when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));

        patientService.deletePatient(patientId);

        verify(patientRepository).existsById(patientId);
        verify(patientRepository, times(1)).deleteById(2L);

        // verify(patientRepository).findById(patientId);
       // verify(patientRepository).delete(patient);
    }

    @Test
    void testDeletePatient_NotFound() {
        Long patientId = 2L;
        when(patientRepository.existsById(patientId)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> patientService.deletePatient(patientId));

        verify(patientRepository).existsById(patientId);
        // DO NOT verify findById since it's not called when existsById returns false
    }

}
