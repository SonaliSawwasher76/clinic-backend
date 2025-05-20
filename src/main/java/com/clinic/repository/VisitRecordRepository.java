package com.clinic.repository;

import com.clinic.entity.Appointment.VisitRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface VisitRecordRepository extends JpaRepository<VisitRecord, Long>, JpaSpecificationExecutor<VisitRecord> {

    Optional<VisitRecord> findByAppointment_AppointmentId(Long appointmentId);

}
