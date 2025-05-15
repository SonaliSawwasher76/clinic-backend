package com.clinic.mapper;

import com.clinic.dto.Doctor.DoctorRequestDTO;
import com.clinic.dto.Doctor.DoctorResponseDTO;
import com.clinic.entity.Doctor;
import com.clinic.entity.user.User;
import com.clinic.entity.user.UserProfile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class DoctorMapper {

    public Doctor doctorRequestDTOToDoctor(DoctorRequestDTO dto) {
        if (dto == null) return null;
        Doctor doctor = new Doctor();
        doctor.setSpecialization(dto.getSpecialization());
        doctor.setLicenseNumber(dto.getLicenseNumber());
        doctor.setYearsOfExperience(dto.getYearsOfExperience());
        return doctor;
    }

    public DoctorResponseDTO doctorToDoctorResponseDTO(Doctor doctor) {
        if (doctor == null) return null;

        User user = doctor.getUser();
        UserProfile profile = user != null ? user.getUserProfile() : null;

        DoctorResponseDTO dto = new DoctorResponseDTO();
        dto.setDoctorId(doctor.getDoctorId());
        if (profile != null) {
            dto.setFirstName(profile.getFirstName());
            dto.setLastName(profile.getLastName());
            dto.setGender(profile.getGender());
            dto.setContactNo(profile.getContactNo());
            dto.setAddress(profile.getAddress());
        }
        if (user != null) {
            dto.setEmail(user.getEmail());
        }
        dto.setSpecialization(doctor.getSpecialization());
        dto.setLicenseNumber(doctor.getLicenseNumber());
        dto.setYearsOfExperience(doctor.getYearsOfExperience());
        return dto;
    }

    public List<DoctorResponseDTO> doctorListToDoctorResponseDTOList(List<Doctor> doctorList) {
        return doctorList.stream()
                .map(this::doctorToDoctorResponseDTO)
                .collect(Collectors.toList());
    }
}
