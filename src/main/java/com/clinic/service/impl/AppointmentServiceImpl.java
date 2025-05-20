package com.clinic.service.impl;

import com.clinic.dto.Appointment.*;
import com.clinic.entity.Appointment.Appointment;
import com.clinic.entity.Appointment.DoctorSchedule;
import com.clinic.entity.Message;
import com.clinic.entity.Doctor;
import com.clinic.entity.Patient;
import com.clinic.enums.AppointmentStatus;
import com.clinic.mapper.AppointmentMapper;
import com.clinic.repository.AppointmentRepository;
import com.clinic.repository.DoctorRepository;
import com.clinic.repository.DoctorScheduleRepository;
import com.clinic.repository.MessageRepository;
import com.clinic.repository.PatientRepository;
import com.clinic.service.services.AppointmentService;
import com.clinic.service.services.AuditLogService;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final DoctorScheduleRepository doctorScheduleRepository;
    private final MessageRepository messageRepository;
    private final AuditLogService auditLogService;  // AuditLogService field


    private final MessageHelper messageHelper = new MessageHelper();

    @Override
    public AppointmentResponseDTO bookAppointment(AppointmentRequestDTO requestDTO) {

        Doctor doctor = doctorRepository.findById(requestDTO.getDoctorId())
                .orElseThrow(() -> new NoSuchElementException("Doctor not found with ID " + requestDTO.getDoctorId()));

        Patient patient = patientRepository.findById(requestDTO.getPatientId())
                .orElseThrow(() -> new NoSuchElementException("Patient not found with ID " + requestDTO.getPatientId()));


        LocalDateTime requestedStart = LocalDateTime.of(
                requestDTO.getAppointmentDate(),
                requestDTO.getStartTime()
        );

        if (requestedStart.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Appointment must be booked for a future date and time");
        }

        LocalDate appointmentDate = requestDTO.getAppointmentDate();
        DayOfWeek dayOfWeek = appointmentDate.getDayOfWeek();

        List<DoctorSchedule> schedules = doctorScheduleRepository.findByDoctorAndDayOfWeek(doctor, dayOfWeek);

        if (schedules.isEmpty()) {
            throw new IllegalArgumentException("Doctor is not available on " + dayOfWeek);
        }

        DoctorSchedule schedule = schedules.get(0);

        LocalTime startTime = requestDTO.getStartTime();
        LocalTime endTime = startTime.plusMinutes(schedule.getAppointmentDurationMinutes());

        if (startTime.isBefore(schedule.getStartTime()) || endTime.isAfter(schedule.getEndTime())) {
            throw new IllegalArgumentException("Appointment time is outside doctor's working hours");
        }

        if (!schedule.getLunchStartTime().equals(schedule.getLunchEndTime()) &&
                (startTime.isBefore(schedule.getLunchEndTime()) && endTime.isAfter(schedule.getLunchStartTime()))) {
            throw new IllegalArgumentException("Appointment time is during doctor's lunch break");
        }

        if (!isSlotAvailable(doctor, appointmentDate, startTime, endTime, null)) {
            throw new IllegalArgumentException("Requested time slot is already booked or unavailable");
        }

        Appointment appointment = AppointmentMapper.toEntity(requestDTO);
        appointment.setDoctor(doctor);
        appointment.setPatient(patient);
        appointment.setEndTime(endTime);
        appointment.setStatus(AppointmentStatus.BOOKED);
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        appointment.setCreatedBy(username);
        appointment.setCreatedAt(LocalDateTime.now());

        Appointment savedAppointment = appointmentRepository.save(appointment);

        // Create and save messages
        saveMessagesForBooking(savedAppointment);
        auditLogService.logAction(
                "Appointment Created for patient",
                "Appointment Module",
                "Appointment Created"
        );

        return AppointmentMapper.toResponseDTO(savedAppointment);
    }

    @Override
    public List<AppointmentResponseDTO> getAppointmentsByDoctor(Long doctorId) {
        List<Appointment> appointments = appointmentRepository.findByDoctorDoctorId(doctorId);
        return appointments.stream()
                .map(AppointmentMapper::toResponseDTO)
                .toList();
    }

    @Override
    public List<AppointmentResponseDTO> getAppointmentsByPatient(Long patientId) {
        List<Appointment> appointments = appointmentRepository.findByPatientId(patientId);
        return appointments.stream()
                .map(AppointmentMapper::toResponseDTO)
                .toList();
    }

    @Override
    public void cancelAppointment(AppointmentCancelDTO cancelDTO, String cancelledByUsername) {
        Appointment appointment = appointmentRepository.findById(cancelDTO.getAppointmentId())
                .orElseThrow(() -> new NoSuchElementException("Appointment not found with ID " + cancelDTO.getAppointmentId()));

        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new IllegalStateException("Appointment already cancelled");
        }

        // If cancelledByUsername is null or empty, get from security context
        if (cancelledByUsername == null || cancelledByUsername.isBlank()) {
            cancelledByUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        }

        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointment.setCancelledBy(cancelledByUsername);
        appointment.setCancelledAt(LocalDateTime.now());
        appointment.setCancellationReason(cancelDTO.getCancellationReason());

        appointmentRepository.save(appointment);
        auditLogService.logAction(
                "Appointment Cancelled for patient",
                "Appointment Module",
                "Appointment Cancelled"
        );

        // Create and save cancel messages
        saveMessagesForCancellation(appointment);
    }

    @Override
    public AppointmentResponseDTO rescheduleAppointment(AppointmentRescheduleDTO rescheduleDTO, String rescheduledByUsername) {
        Appointment appointment = appointmentRepository.findById(rescheduleDTO.getAppointmentId())
                .orElseThrow(() -> new NoSuchElementException("Appointment not found with ID " + rescheduleDTO.getAppointmentId()));

        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new IllegalStateException("Cancelled appointment cannot be rescheduled");
        }


        // If rescheduledByUsername is null or empty, get from security context
        if (rescheduledByUsername == null || rescheduledByUsername.isBlank()) {
            rescheduledByUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        }

        Doctor doctor = appointment.getDoctor();
        LocalDate newDate = rescheduleDTO.getNewDate();
        LocalTime newStartTime = rescheduleDTO.getNewStartTime();
        DayOfWeek newDay = newDate.getDayOfWeek();

        List<DoctorSchedule> schedules = doctorScheduleRepository.findByDoctorAndDayOfWeek(doctor, newDay);

        if (schedules.isEmpty()) {
            throw new IllegalArgumentException("Doctor is not available on " + newDay);
        }

        DoctorSchedule schedule = schedules.get(0);

        LocalTime newEndTime = newStartTime.plusMinutes(schedule.getAppointmentDurationMinutes());

        if (newStartTime.isBefore(schedule.getStartTime()) || newEndTime.isAfter(schedule.getEndTime())) {
            throw new IllegalArgumentException("Rescheduled time is outside doctor's working hours");
        }

        if (!schedule.getLunchStartTime().equals(schedule.getLunchEndTime()) &&
                (newStartTime.isBefore(schedule.getLunchEndTime()) && newEndTime.isAfter(schedule.getLunchStartTime()))) {
            throw new IllegalArgumentException("Rescheduled time is during doctor's lunch break");
        }

        LocalDateTime newStart = LocalDateTime.of(newDate, newStartTime);
        if (newStart.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Rescheduled slot must be in the future");
        }

        if (!isSlotAvailable(doctor, newDate, newStartTime, newEndTime, appointment.getAppointmentId())) {
            throw new IllegalArgumentException("Requested reschedule slot is not available");
        }

        appointment.setStatus(AppointmentStatus.RESCHEDULED);
        appointment.setRescheduledBy(rescheduledByUsername);
        appointment.setRescheduledFrom(LocalDateTime.of(appointment.getAppointmentDate(), appointment.getStartTime()));
        appointment.setRescheduledTo(LocalDateTime.of(newDate, newStartTime));
        appointment.setRescheduleReason(rescheduleDTO.getRescheduleReason());
        appointment.setAppointmentDate(newDate);
        appointment.setStartTime(newStartTime);
        appointment.setEndTime(newEndTime);

        Appointment updated = appointmentRepository.save(appointment);

        // Create and save reschedule messages
        saveMessagesForReschedule(updated);
        auditLogService.logAction(
                "Appointment rescheduled for patient",
                "Appointment Module",
                "Appointment Recreated"
        );

        return AppointmentMapper.toResponseDTO(updated);
    }

    @Override
    public List<AppointmentResponseDTO> searchAppointments(Long doctorId, Long patientId,
                                                           LocalDate appointmentDate, LocalTime startTime, LocalTime endTime,
                                                           AppointmentStatus status, String createdBy, String cancelledBy, String rescheduledBy) {

        Specification<Appointment> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (doctorId != null)
                predicates.add(cb.equal(root.get("doctor").get("doctorId"), doctorId));
            if (patientId != null)
                predicates.add(cb.equal(root.get("patient").get("patientId"), patientId));
            if (appointmentDate != null)
                predicates.add(cb.equal(root.get("appointmentDate"), appointmentDate));
            if (startTime != null)
                predicates.add(cb.equal(root.get("startTime"), startTime));
            if (endTime != null)
                predicates.add(cb.equal(root.get("endTime"), endTime));
            if (status != null)
                predicates.add(cb.equal(root.get("status"), status));
            if (createdBy != null && !createdBy.isBlank())
                predicates.add(cb.equal(root.get("createdBy"), createdBy));
            if (cancelledBy != null && !cancelledBy.isBlank())
                predicates.add(cb.equal(root.get("cancelledBy"), cancelledBy));
            if (rescheduledBy != null && !rescheduledBy.isBlank())
                predicates.add(cb.equal(root.get("rescheduledBy"), rescheduledBy));

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return appointmentRepository.findAll(spec)
                .stream()
                .map(AppointmentMapper::toResponseDTO)
                .toList();
    }

    private boolean isSlotAvailable(Doctor doctor, LocalDate date, LocalTime startTime, LocalTime endTime, Long excludeAppointmentId) {

        List<Appointment> overlappingAppointments = appointmentRepository.findByDoctorAndAppointmentDateAndTimeRange(
                doctor, date, startTime, endTime);

        for (Appointment appt : overlappingAppointments) {
            if (excludeAppointmentId != null && appt.getAppointmentId().equals(excludeAppointmentId)) {
                continue; // Skip a current appointment if rescheduling
            }
            if (appt.getStatus() == AppointmentStatus.CANCELLED) {
                continue; // Ignore cancelled appointments
            }
            return false; // Overlap found
        }
        return true; // No overlap
    }

    // Save messages on booking
    private void saveMessagesForBooking(Appointment appointment) {
        Message adminMsg = Message.builder()
                .recipientType(Message.RecipientType.ADMIN)
                .recipientEmail("admin@example.com")  // Replace with actual admin email if available
                .subject("Appointment Booked")
                .body(messageHelper.adminBookingMessage(appointment))
                .createdAt(LocalDateTime.now())
                .build();

        Message doctorMsg = Message.builder()
                .recipientType(Message.RecipientType.DOCTOR)
                .recipientEmail(appointment.getDoctor().getUser().getEmail()) // Assuming getEmail() exists
                .subject("New Appointment")
                .body(messageHelper.doctorBookingMessage(appointment))
                .createdAt(LocalDateTime.now())
                .build();

        Message patientMsg = Message.builder()
                .recipientType(Message.RecipientType.PATIENT)
                .recipientEmail(appointment.getPatient().getEmail()) // Assuming getEmail() exists
                .subject("Your Appointment is Booked")
                .body(messageHelper.patientBookingMessage(appointment))
                .createdAt(LocalDateTime.now())
                .build();

        messageRepository.save(adminMsg);
        messageRepository.save(doctorMsg);
        messageRepository.save(patientMsg);
    }

    private void saveMessagesForCancellation(Appointment appointment) {
        Message adminMsg = Message.builder()
                .recipientType(Message.RecipientType.ADMIN)
                .recipientEmail("admin@example.com")
                .subject("Appointment Cancelled")
                .body(messageHelper.adminCancelMessage(appointment))
                .createdAt(LocalDateTime.now())
                .build();

        Message doctorMsg = Message.builder()
                .recipientType(Message.RecipientType.DOCTOR)
                .recipientEmail(appointment.getDoctor().getUser().getEmail())
                .subject("Appointment Cancelled")
                .body(messageHelper.doctorCancelMessage(appointment))
                .createdAt(LocalDateTime.now())
                .build();

        Message patientMsg = Message.builder()
                .recipientType(Message.RecipientType.PATIENT)
                .recipientEmail(appointment.getPatient().getEmail())
                .subject("Your Appointment Cancelled")
                .body(messageHelper.patientCancelMessage(appointment))
                .createdAt(LocalDateTime.now())
                .build();

        messageRepository.save(adminMsg);
        messageRepository.save(doctorMsg);
        messageRepository.save(patientMsg);
    }

    private void saveMessagesForReschedule(Appointment appointment) {
        Message adminMsg = Message.builder()
                .recipientType(Message.RecipientType.ADMIN)
                .recipientEmail("admin@example.com")
                .subject("Appointment Rescheduled")
                .body(messageHelper.adminRescheduleMessage(appointment))
                .createdAt(LocalDateTime.now())
                .build();

        Message doctorMsg = Message.builder()
                .recipientType(Message.RecipientType.DOCTOR)
                .recipientEmail(appointment.getDoctor().getUser().getEmail())
                .subject("Appointment Rescheduled")
                .body(messageHelper.doctorRescheduleMessage(appointment))
                .createdAt(LocalDateTime.now())
                .build();

        Message patientMsg = Message.builder()
                .recipientType(Message.RecipientType.PATIENT)
                .recipientEmail(appointment.getPatient().getEmail())
                .subject("Your Appointment Rescheduled")
                .body(messageHelper.patientRescheduleMessage(appointment))
                .createdAt(LocalDateTime.now())
                .build();

        messageRepository.save(adminMsg);
        messageRepository.save(doctorMsg);
        messageRepository.save(patientMsg);
    }

    // Inner helper class for messages
    private static class MessageHelper {

        String adminBookingMessage(Appointment a) {
            return String.format(
                    "Appointment Booked: Doctor %s, Patient %s, Date %s, Time %s - %s (%s)",
                    a.getDoctor().getUser().getUserProfile().getFirstName(),
                    a.getPatient().getFirstname(),
                    a.getAppointmentDate(),
                    a.getStartTime(),
                    a.getEndTime(),
                    a.getAppointmentDate().getDayOfWeek()
            );
        }

        String doctorBookingMessage(Appointment a) {
            return String.format(
                    "New Appointment with Patient %s on %s %s - %s (%s)",
                    a.getPatient().getFirstname(),
                    a.getAppointmentDate(),
                    a.getStartTime(),
                    a.getEndTime(),
                    a.getAppointmentDate().getDayOfWeek()
            );
        }

        String patientBookingMessage(Appointment a) {
            return String.format(
                    "Your appointment with Doctor %s is booked for %s %s - %s (%s)",
                    a.getDoctor().getUser().getUserProfile().getFirstName(),
                    a.getAppointmentDate(),
                    a.getStartTime(),
                    a.getEndTime(),
                    a.getAppointmentDate().getDayOfWeek()
            );
        }

        String adminCancelMessage(Appointment a) {
            return String.format(
                    "Appointment Cancelled: Doctor %s, Patient %s, Date %s, Time %s - %s (%s), Reason: %s",
                    a.getDoctor().getUser().getUserProfile().getFirstName(),
                    a.getPatient().getFirstname(),
                    a.getAppointmentDate(),
                    a.getStartTime(),
                    a.getEndTime(),
                    a.getAppointmentDate().getDayOfWeek(),
                    a.getCancellationReason()
            );
        }

        String doctorCancelMessage(Appointment a) {
            return String.format(
                    "Appointment with Patient %s on %s %s - %s (%s) was cancelled. Reason: %s",
                    a.getPatient().getFirstname(),
                    a.getAppointmentDate(),
                    a.getStartTime(),
                    a.getEndTime(),
                    a.getAppointmentDate().getDayOfWeek(),
                    a.getCancellationReason()
            );
        }

        String patientCancelMessage(Appointment a) {
            return String.format(
                    "Your appointment with Doctor %s on %s %s - %s (%s) was cancelled. Reason: %s",
                    a.getDoctor().getUser().getUserProfile().getFirstName(),
                    a.getAppointmentDate(),
                    a.getStartTime(),
                    a.getEndTime(),
                    a.getAppointmentDate().getDayOfWeek(),
                    a.getCancellationReason()
            );
        }

        String adminRescheduleMessage(Appointment a) {
            return String.format(
                    "Appointment Rescheduled: Doctor %s, Patient %s, New Date %s, New Time %s - %s (%s), Reason: %s",
                    a.getDoctor().getUser().getUserProfile().getFirstName(),
                    a.getPatient().getFirstname(),
                    a.getAppointmentDate(),
                    a.getStartTime(),
                    a.getEndTime(),
                    a.getAppointmentDate().getDayOfWeek(),
                    a.getRescheduleReason()
            );
        }

        String doctorRescheduleMessage(Appointment a) {
            return String.format(
                    "Appointment with Patient %s rescheduled to %s %s - %s (%s). Reason: %s",
                    a.getPatient().getFirstname(),
                    a.getAppointmentDate(),
                    a.getStartTime(),
                    a.getEndTime(),
                    a.getAppointmentDate().getDayOfWeek(),
                    a.getRescheduleReason()
            );
        }

        String patientRescheduleMessage(Appointment a) {
            return String.format(
                    "Your appointment with Doctor %s has been rescheduled to %s %s - %s (%s). Reason: %s",
                    a.getDoctor().getUser().getUserProfile().getFirstName(),
                    a.getAppointmentDate(),
                    a.getStartTime(),
                    a.getEndTime(),
                    a.getAppointmentDate().getDayOfWeek(),
                    a.getRescheduleReason()
            );
        }
    }
}
