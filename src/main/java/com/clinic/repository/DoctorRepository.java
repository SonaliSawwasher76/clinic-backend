package com.clinic.repository;

import com.clinic.entity.Doctor;
import com.clinic.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor, Long>, JpaSpecificationExecutor<Doctor> {
    Optional<Doctor> findByUser(User user);
    //boolean existsByemailAndDoctorIdNot(String email, Long doctorId);
    Optional<Doctor> findByUserUserId(Long userId);

    @Query("SELECT d FROM Doctor d WHERE d.user.workspace.workspaceId = :workspaceId")
    List<Doctor> findDoctorsByUserWorkSpaceId(@Param("workspaceId") String workspaceId);


}
