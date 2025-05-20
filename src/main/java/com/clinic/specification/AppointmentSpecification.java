package com.clinic.specification;

import com.clinic.entity.Appointment.Appointment;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalTime;

public class AppointmentSpecification {

    public static Specification<Appointment> hasDoctorId(Long doctorId) {
        return (root, query, cb) -> doctorId == null ? null : cb.equal(root.get("doctor").get("doctorId"), doctorId);
    }

    public static Specification<Appointment> hasPatientId(Long patientId) {
        return (root, query, cb) -> patientId == null ? null : cb.equal(root.get("patient").get("id"), patientId);
    }

    public static Specification<Appointment> hasAppointmentDate(LocalDate date) {
        return (root, query, cb) -> date == null ? null : cb.equal(root.get("appointmentDate"), date);
    }

    public static Specification<Appointment> hasStartTime(LocalTime startTime) {
        return (root, query, cb) -> startTime == null ? null : cb.equal(root.get("startTime"), startTime);
    }

    public static Specification<Appointment> hasEndTime(LocalTime endTime) {
        return (root, query, cb) -> endTime == null ? null : cb.equal(root.get("endTime"), endTime);
    }
}
