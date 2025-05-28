package com.clinic.repository;

import com.clinic.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Long>, JpaSpecificationExecutor<Patient> {
    Optional<Patient> findByEmail(String email);

    @Query("SELECT p FROM Patient p WHERE " +
            "LOWER(p.firstname) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(p.lastname) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(p.email) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(p.gender) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "CAST(p.id AS string) LIKE %:query% OR " +
            "p.contactNumber LIKE %:query%")
    List<Patient> searchByQuery(@Param("query") String query);

}
