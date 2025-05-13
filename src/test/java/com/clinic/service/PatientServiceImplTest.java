package com.clinic.service;

import com.clinic.dto.Patient.PatientRequestDTO;
import com.clinic.dto.Patient.PatientResponseDTO;
import com.clinic.entity.Patient;
import com.clinic.exception.ResourceNotFoundException;
import com.clinic.mapper.PatientMapper;
import com.clinic.repository.PatientRepository;

import com.clinic.service.impl.PatientServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PatientServiceImplTest {

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private PatientMapper patientMapper;

    @InjectMocks
    private PatientServiceImpl patientService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetPatientById_Success() {
        Patient patient = Patient.builder()
                .id(1L)
                .firstname("John Doe")
                .dob(LocalDate.parse("25-03-2007"))
                .gender("Male")
                .contactNumber("1234567890")
                .address("123 Main St")
                .email("john@example.com")
                .build();

        PatientResponseDTO responseDTO = PatientResponseDTO.builder()
                .id(1L)
                .firstname("John Doe")
                .dob(LocalDate.parse("25-03-2007"))
                .gender("Male")
                .contactNumber("1234567890")
                .address("123 Main St")
                .email("john@example.com")
                .build();

        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(patientMapper.patientToPatientResponseDTO(patient)).thenReturn(responseDTO);

        PatientResponseDTO result = patientService.getPatientById(1L);

        assertNotNull(result);
        assertEquals("John Doe", result.getFirstname());
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
                .firstname("Jane Doe")
                .dob(LocalDate.parse("25-03-2007"))
                .gender("Female")
                .contactNumber("9876543210")
                .address("456 Park Ave")
                .email("jane@example.com")
                .build();

        Patient patient = Patient.builder()
                .id(2L)
                .firstname("Jane Doe")
                .dob(LocalDate.parse("25-03-2007"))
                .gender("Female")
                .contactNumber("9876543210")
                .address("456 Park Ave")
                .email("jane@example.com")
                .build();

        PatientResponseDTO responseDTO = PatientResponseDTO.builder()
                .id(2L)
                .firstname("Jane Doe")
                .dob(LocalDate.parse("25-03-2007"))
                .gender("Female")
                .contactNumber("9876543210")
                .address("456 Park Ave")
                .email("jane@example.com")
                .build();

        when(patientMapper.patientRequestDTOToPatient(requestDTO)).thenReturn(patient);
        when(patientRepository.save(patient)).thenReturn(patient);
        when(patientMapper.patientToPatientResponseDTO(patient)).thenReturn(responseDTO);

        PatientResponseDTO result = patientService.createPatient(requestDTO);

        assertNotNull(result);
        assertEquals("Jane Doe", result.getFirstname());
        verify(patientRepository, times(1)).save(patient);
    }
}
