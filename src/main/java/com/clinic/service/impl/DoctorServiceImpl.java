package com.clinic.service.impl;

import com.clinic.dto.Doctor.DoctorRequestDTO;
import com.clinic.dto.Doctor.DoctorResponseDTO;
import com.clinic.dto.Auth.UserSignupRequestDTO;
import com.clinic.dto.Auth.SignupRequestWrapperDTO;
import com.clinic.entity.Doctor;
import com.clinic.entity.user.User;
import com.clinic.entity.user.UserProfile;
import com.clinic.enums.Role;
import com.clinic.mapper.DoctorMapper;
import com.clinic.repository.DoctorRepository;
import com.clinic.repository.UserRepository;
import com.clinic.service.DoctorService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DoctorServiceImpl implements DoctorService {

    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final DoctorMapper doctorMapper;
    private final PasswordEncoder passwordEncoder;

    public DoctorServiceImpl(UserRepository userRepository, DoctorRepository doctorRepository,
                             DoctorMapper doctorMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.doctorRepository = doctorRepository;
        this.doctorMapper = doctorMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public DoctorResponseDTO createDoctor(SignupRequestWrapperDTO signupRequestWrapperDTO) {
        UserSignupRequestDTO userSignupRequestDTO = signupRequestWrapperDTO.getUser();
        DoctorRequestDTO doctorRequestDTO = signupRequestWrapperDTO.getDoctor();

        if (!userSignupRequestDTO.isAgeValid()) {
            throw new IllegalArgumentException("Age must be greater than 18");
        }

        // 1. Create a User entity with a role DOCTOR
        User user = new User();
        user.setEmail(userSignupRequestDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userSignupRequestDTO.getPassword()));  // Encode password
        user.setRole(Role.DOCTOR);  // Set role as DOCTOR
        userRepository.save(user);  // Save user to users table

        // 2. Create a UserProfile for the doctor
        UserProfile userProfile = new UserProfile();
        userProfile.setFirstName(userSignupRequestDTO.getFirstName());
        userProfile.setLastName(userSignupRequestDTO.getLastName());
        userProfile.setContactNo(userSignupRequestDTO.getContactNo());
        userProfile.setDob(userSignupRequestDTO.getDob());
        userProfile.setGender(userSignupRequestDTO.getGender());
        userProfile.setAddress(userSignupRequestDTO.getAddress());
        user.setUserProfile(userProfile);

        userRepository.save(user);
       // userRepository.save(userProfile);

        // 3. Create a Doctor entity and link to the user and user profile
        Doctor doctor = doctorMapper.doctorRequestDTOToDoctor(doctorRequestDTO);
        doctor.setUser(user);  // Set the user entity (one-to-one relationship)
        doctorRepository.save(doctor);  // Save to doctors' table

        // Return a response DTO
        return doctorMapper.doctorToDoctorResponseDTO(doctor);
    }

    @Override
    public DoctorResponseDTO updateDoctor(Long doctorId, DoctorRequestDTO doctorRequestDTO) {
        // Find an existing doctor
        Doctor existingDoctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        // Update doctor details
        existingDoctor.setSpecialization(doctorRequestDTO.getSpecialization());
        existingDoctor.setLicenseNumber(doctorRequestDTO.getLicenseNumber());
        existingDoctor.setYearsOfExperience(doctorRequestDTO.getYearsOfExperience());

        // Save updated doctor data
        Doctor updatedDoctor = doctorRepository.save(existingDoctor);

        return doctorMapper.doctorToDoctorResponseDTO(updatedDoctor);
    }

    @Override
    public void deleteDoctor(Long doctorId) {
        // Check if the doctor exists
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        // Delete doctor record
        doctorRepository.delete(doctor);
    }

    @Override
    public DoctorResponseDTO getDoctor(Long doctorId) {
        // Fetch doctor details
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        return doctorMapper.doctorToDoctorResponseDTO(doctor);
    }

    @Override
    public List<DoctorResponseDTO> getAllDoctors() {
        List<Doctor> doctors = doctorRepository.findAll();
        return doctorMapper.doctorListToDoctorResponseDTOList(doctors);
    }
}
