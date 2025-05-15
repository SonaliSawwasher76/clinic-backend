package com.clinic.service.impl;

import com.clinic.dto.Auth.SignupRequestWrapperDTO;
import com.clinic.dto.Auth.UserSignupRequestDTO;
import com.clinic.dto.Doctor.DoctorRequestDTO;
import com.clinic.dto.Doctor.DoctorResponseDTO;
import com.clinic.entity.Doctor;
import com.clinic.entity.user.User;
import com.clinic.entity.user.UserProfile;
import com.clinic.enums.Role;
import com.clinic.exception.InvalidInputException;
import com.clinic.mapper.DoctorMapper;
import com.clinic.repository.DoctorRepository;
import com.clinic.repository.UserRepository;
import com.clinic.service.AuditLogService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DoctorServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private DoctorMapper doctorMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuditLogService auditLogService;

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private DoctorServiceImpl doctorService;




    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(doctorService, "entityManager", entityManager);
    }

    @Test
    void createDoctor_validRequest_successfulCreation() {
        // Arrange
        UserSignupRequestDTO userDTO = mock(UserSignupRequestDTO.class);
        when(userDTO.getEmail()).thenReturn("test@example.com");
        when(userDTO.getPassword()).thenReturn("plainPassword");
        when(userDTO.getFirstName()).thenReturn("John");
        when(userDTO.getLastName()).thenReturn("Doe");
        when(userDTO.getContactNo()).thenReturn("1234567890");
        when(userDTO.getDob()).thenReturn(java.time.LocalDate.of(1990, 1, 1));
        when(userDTO.getGender()).thenReturn("Male");
        when(userDTO.getAddress()).thenReturn("Test Address");
        when(userDTO.isAgeValid()).thenReturn(true);

        DoctorRequestDTO doctorDTO = new DoctorRequestDTO();
        doctorDTO.setSpecialization("Cardiology");
        doctorDTO.setLicenseNumber("LIC123");
        doctorDTO.setYearsOfExperience(10);

        SignupRequestWrapperDTO wrapperDTO = new SignupRequestWrapperDTO();
        wrapperDTO.setUser(userDTO);
        wrapperDTO.setDoctor(doctorDTO);

        User user = new User();
        user.setUserProfile(new UserProfile());
        user.setRole(Role.DOCTOR);

        Doctor doctor = new Doctor();
        doctor.setUser(user);

        DoctorResponseDTO responseDTO = new DoctorResponseDTO();
        responseDTO.setSpecialization("Cardiology");

        // Mocks
        when(passwordEncoder.encode(userDTO.getPassword())).thenReturn("encodedPassword");
        when(doctorMapper.doctorRequestDTOToDoctor(doctorDTO)).thenReturn(doctor);
        when(doctorMapper.doctorToDoctorResponseDTO(any(Doctor.class))).thenReturn(responseDTO);

        // Act
        DoctorResponseDTO result = doctorService.createDoctor(wrapperDTO);

        // Assert
        assertNotNull(result);
        assertEquals("Cardiology", result.getSpecialization());

        verify(userRepository, times(2)).save(any(User.class)); // first for User, second after setting profile
        verify(doctorRepository).save(any(Doctor.class));
        verify(auditLogService).logAction(eq("CREATE_DOCTOR"), eq("DOCTORModule"), eq("DOCTOR created"));
    }

    @Test
    void createDoctor_invalidAge_throwsInvalidInputException() {
        // Arrange
        UserSignupRequestDTO userDTO = mock(UserSignupRequestDTO.class);
        when(userDTO.isAgeValid()).thenReturn(false);

        SignupRequestWrapperDTO wrapperDTO = new SignupRequestWrapperDTO();
        wrapperDTO.setUser(userDTO);
        wrapperDTO.setDoctor(new DoctorRequestDTO());

        // Act & Assert
        assertThrows(InvalidInputException.class, () -> doctorService.createDoctor(wrapperDTO));
    }

    @Test
    void updateDoctor_validRequest_successfulUpdate() {
        Long doctorId = 1L;

        // Create DoctorRequestDTO with updated doctor info
        DoctorRequestDTO doctorDTO = new DoctorRequestDTO();
        doctorDTO.setSpecialization("Neurology");
        doctorDTO.setLicenseNumber("LIC456");
        doctorDTO.setYearsOfExperience(8);

        // Create UserSignupRequestDTO (non-null!)
        UserSignupRequestDTO userDTO = new UserSignupRequestDTO();
        userDTO.setEmail("test@example.com");
        userDTO.setPassword("password");   // Set whatever required fields here

        // Wrap them together
        SignupRequestWrapperDTO wrapperDTO = new SignupRequestWrapperDTO();
        wrapperDTO.setUser(userDTO);
        wrapperDTO.setDoctor(doctorDTO);

        // Mock existing doctor and other setup
        Doctor existingDoctor = new Doctor();
        existingDoctor.setDoctorId(doctorId);
        existingDoctor.setSpecialization("Cardiology");
        existingDoctor.setLicenseNumber("LIC123");
        existingDoctor.setYearsOfExperience(5);
        existingDoctor.setUser(new User());

        Doctor updatedDoctor = new Doctor();
        updatedDoctor.setDoctorId(doctorId);
        updatedDoctor.setSpecialization("Neurology");
        updatedDoctor.setLicenseNumber("LIC456");
        updatedDoctor.setYearsOfExperience(8);
        updatedDoctor.setUser(existingDoctor.getUser());

        DoctorResponseDTO responseDTO = new DoctorResponseDTO();
        responseDTO.setSpecialization("Neurology");

        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(existingDoctor));
        when(doctorRepository.save(any(Doctor.class))).thenReturn(updatedDoctor);
        when(doctorMapper.doctorToDoctorResponseDTO(any(Doctor.class))).thenReturn(responseDTO);

        // Call method
        DoctorResponseDTO result = doctorService.updateDoctor(doctorId, wrapperDTO);

        assertNotNull(result);
        assertEquals("Neurology", result.getSpecialization());

        verify(doctorRepository).save(existingDoctor);
        verify(auditLogService).logAction(eq("UPDATE_DOCTOR"), eq("DOCTORModule"), contains("updated"));
    }

    @Test
    void deleteDoctor_existingDoctor_deletesSuccessfully() {
        Long doctorId = 1L;

        // Arrange entities
        UserProfile userProfile = new UserProfile();
        userProfile.setUserProfileId(10L);

        User user = new User();
        user.setUserProfile(userProfile);

        Doctor doctor = new Doctor();
        doctor.setDoctorId(doctorId);
        doctor.setUser(user);

        // Mock repository calls
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));
        doNothing().when(doctorRepository).delete(any(Doctor.class));
        doNothing().when(userRepository).delete(any(User.class));
        // For entityManager.remove, we need to mock find and remove calls
        when(entityManager.find(UserProfile.class, userProfile.getUserProfileId())).thenReturn(userProfile);
        doNothing().when(entityManager).remove(userProfile);

        // Act
        doctorService.deleteDoctor(doctorId);

        // Assert
        verify(doctorRepository).findById(doctorId);
        verify(doctorRepository).delete(doctor);
        verify(userRepository).delete(user);
        verify(entityManager).find(UserProfile.class, userProfile.getUserProfileId());
        verify(entityManager).remove(userProfile);
        verify(auditLogService).logAction(eq("DELETE_DOCTOR"), eq("DOCTORModule"), contains("Doctor deleted with ID"));

    }


}
