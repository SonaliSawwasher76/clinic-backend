package com.clinic.service.impl;

import com.clinic.dto.Doctor.DoctorRequestDTO;
import com.clinic.dto.Doctor.DoctorResponseDTO;
import com.clinic.dto.Auth.UserSignupRequestDTO;
import com.clinic.dto.Auth.SignupRequestWrapperDTO;

import com.clinic.entity.Doctor;
import com.clinic.entity.Workspace;
import com.clinic.entity.user.User;
import com.clinic.entity.user.UserProfile;
import com.clinic.enums.Role;
import com.clinic.exception.InvalidInputException;
import com.clinic.exception.ResourceNotFoundException;
import com.clinic.mapper.DoctorMapper;
import com.clinic.repository.DoctorRepository;
import com.clinic.repository.UserRepository;
import com.clinic.repository.WorkspaceRepository;
import com.clinic.service.services.AuditLogService;
import com.clinic.service.services.DoctorService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

 // inject in constructor


import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;


import java.util.List;
import java.util.stream.Collectors;

@Service
public class DoctorServiceImpl implements DoctorService {

    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final DoctorMapper doctorMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuditLogService auditLogService;  // AuditLogService field
    private final WorkspaceRepository workspaceRepository;

    public DoctorServiceImpl(UserRepository userRepository, DoctorRepository doctorRepository,
                             DoctorMapper doctorMapper, PasswordEncoder passwordEncoder,AuditLogService auditLogService,WorkspaceRepository workspaceRepository) {
        this.userRepository = userRepository;
        this.doctorRepository = doctorRepository;
        this.doctorMapper = doctorMapper;
        this.passwordEncoder = passwordEncoder;
        this.auditLogService= auditLogService;
        this.workspaceRepository = workspaceRepository;

    }
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public DoctorResponseDTO createDoctor(SignupRequestWrapperDTO signupRequestWrapperDTO) {
        UserSignupRequestDTO userSignupRequestDTO = signupRequestWrapperDTO.getUser();
        DoctorRequestDTO doctorRequestDTO = signupRequestWrapperDTO.getDoctor();

        if (!userSignupRequestDTO.isAgeValid()) {
            throw new InvalidInputException("Age must be greater than 18");
        }

        Workspace workspace = workspaceRepository.findById(userSignupRequestDTO.getWorkspaceId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Workspace not found with ID " + userSignupRequestDTO.getWorkspaceId()));

        // 1. Create a User entity with a role DOCTOR
        User user = new User();
        user.setEmail(userSignupRequestDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userSignupRequestDTO.getPassword()));  // Encode password
        user.setRole(Role.DOCTOR);  // Set role as DOCTOR
        user.setWorkspace(workspace);
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

        auditLogService.logAction(
                "CREATE_DOCTOR",
                "DOCTORModule",
                "DOCTOR created"
        );
        // Return a response DTO
        return doctorMapper.doctorToDoctorResponseDTO(doctor);
    }

    @Override
    public DoctorResponseDTO updateDoctor(Long doctorId, SignupRequestWrapperDTO request) {
        // Step 1: Get doctor from DB
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + doctorId));

        // Step 2: Get associated user
        User user = doctor.getUser();
        if (user == null) {
            throw new ResourceNotFoundException("User not found for doctor ID: " + doctorId);
        }

        // Step 3: Update User entity
        UserSignupRequestDTO userDTO = request.getUser();

        user.setEmail(userDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword())); // re-encode a new password if updating
        user.setRole(userDTO.getRole());
        user.setWorkspace(user.getWorkspace());

        // Step 4: Update UserProfile
        UserProfile profile = user.getUserProfile();
        if (profile == null) {
            profile = new UserProfile();
            //profile.set(user);
            user.setUserProfile(profile);
        }

        profile.setFirstName(userDTO.getFirstName());
        profile.setLastName(userDTO.getLastName());
        profile.setDob(userDTO.getDob());
        profile.setContactNo(userDTO.getContactNo());
        profile.setGender(userDTO.getGender());
        profile.setAddress(userDTO.getAddress());


        // Step 5: Update Doctor entity
        DoctorRequestDTO doctorDTO = request.getDoctor();
        doctor.setSpecialization(doctorDTO.getSpecialization());
        doctor.setLicenseNumber(doctorDTO.getLicenseNumber());
        doctor.setYearsOfExperience(doctorDTO.getYearsOfExperience());

        // Step 6: Save all
        userRepository.save(user);
        doctorRepository.save(doctor);

        auditLogService.logAction("UPDATE_DOCTOR", "DOCTORModule", "Doctor updated with id " + doctor.getDoctorId());


        // Step 7: Convert to DTO and return
        return doctorMapper.doctorToDoctorResponseDTO(doctor);
    }



    @Override
    @Transactional
    public void deleteDoctor(Long doctorId) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with ID: " + doctorId));

        User user = doctor.getUser(); // Linked user
        UserProfile profile = user.getUserProfile(); // Linked profile

        // Step 1: Remove Doctor
        doctor.setUser(null); // break association
        doctorRepository.delete(doctor);

        // Step 2: Remove User
        user.setUserProfile(null); // break association
        userRepository.delete(user);

        // Step 3: Remove UserProfile manually via EntityManager
        if (profile != null) {
            UserProfile attachedProfile = entityManager.find(UserProfile.class, profile.getUserProfileId());
            if (attachedProfile != null) {
                entityManager.remove(attachedProfile);
            }
        }

        auditLogService.logAction("DELETE_DOCTOR", "DOCTORModule", "Doctor deleted with ID: " + doctorId);
    }



    @Override
    public DoctorResponseDTO getDoctor(Long doctorId) {
        // Fetch doctor details
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor with ID " + doctorId + " not found"));

        return doctorMapper.doctorToDoctorResponseDTO(doctor);
    }

    @Override
    public List<DoctorResponseDTO> getAllDoctors() {
        List<Doctor> doctors = doctorRepository.findAll();
        return doctorMapper.doctorListToDoctorResponseDTOList(doctors);
    }

    @Override
    public List<DoctorResponseDTO> searchDoctors(String firstName, String lastName, Long doctorId,
                                                 String specialization, String licenseNumber,
                                                 Integer yearsOfExperience, String contactNo) {


        System.out.println("Inputs:");
        System.out.println("firstName = " + firstName);
        System.out.println("lastName = " + lastName);
        System.out.println("doctorId = " + doctorId);
        System.out.println("specialization = " + specialization);
        System.out.println("licenseNumber = " + licenseNumber);
        System.out.println("yearsOfExperience = " + yearsOfExperience);
        System.out.println("contactNo = " + contactNo);

        Specification<Doctor> spec = (root, query, cb) -> {
            // Join to User and UserProfile
            root.fetch("user").fetch("userProfile");

            Join<Object, Object> userJoin = root.join("user");
            Join<Object, Object> profileJoin = userJoin.join("userProfile");

            Predicate predicate = cb.conjunction();

            if (doctorId != null) {
                predicate = cb.and(predicate, cb.equal(root.get("doctorId"), doctorId));
            }

            if (specialization != null && !specialization.trim().isEmpty()) {
                predicate = cb.and(predicate,
                        cb.like(cb.lower(root.get("specialization")), "%" + specialization.trim().toLowerCase() + "%"));
            }

            if (licenseNumber != null && !licenseNumber.trim().isEmpty()) {
                predicate = cb.and(predicate,
                        cb.like(cb.lower(root.get("licenseNumber")), "%" + licenseNumber.trim().toLowerCase() + "%"));
            }

            if (yearsOfExperience != null) {
                predicate = cb.and(predicate,
                        cb.equal(root.get("yearsOfExperience"), yearsOfExperience));
            }

            if (firstName != null && !firstName.trim().isEmpty()) {
                predicate = cb.and(predicate,
                        cb.like(cb.lower(profileJoin.get("firstName")), "%" + firstName.trim().toLowerCase() + "%"));
            }

            if (lastName != null && !lastName.trim().isEmpty()) {
                predicate = cb.and(predicate,
                        cb.like(cb.lower(profileJoin.get("lastName")), "%" + lastName.trim().toLowerCase() + "%"));
            }

            if (contactNo != null && !contactNo.trim().isEmpty()) {
                predicate = cb.and(predicate,
                        cb.like(cb.lower(profileJoin.get("contactNo")), "%" + contactNo.trim().toLowerCase() + "%"));
            }
            System.out.println("Predicate built: " + predicate);

            return predicate;
        };

        List<Doctor> doctors = doctorRepository.findAll(spec);

        return doctors.stream()
                .map(doctorMapper::doctorToDoctorResponseDTO)
                .collect(Collectors.toList());
    }



}
