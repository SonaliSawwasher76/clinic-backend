package com.clinic.repository;

import com.clinic.entity.Doctor;
import com.clinic.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor, Long>, JpaSpecificationExecutor<Doctor> {
    Optional<Doctor> findByUser(User user);
    //boolean existsByemailAndDoctorIdNot(String email, Long doctorId);

}
