package com.clinic.controller;

import com.clinic.dto.Appointment.*;
import com.clinic.enums.AppointmentStatus;
import com.clinic.service.services.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    /* ---------- create ---------- */
    // @PreAuthorize("hasRole('ADMIN') or hasRole('RECEPTIONIST')")
    @PostMapping
    public ResponseEntity<AppointmentResponseDTO> bookAppointment(@Valid @RequestBody AppointmentRequestDTO requestDTO) {
        System.out.println("→  Controller sees auth = "
                + SecurityContextHolder.getContext().getAuthentication());

        AppointmentResponseDTO dto = appointmentService.bookAppointment(requestDTO);
        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }

    /* ---------- basic fetch ---------- */
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<AppointmentResponseDTO>> getByDoctor(@PathVariable Long doctorId) {
        return ResponseEntity.ok(appointmentService.getAppointmentsByDoctor(doctorId));
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<AppointmentResponseDTO>> getByPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(appointmentService.getAppointmentsByPatient(patientId));
    }

    /* ---------- cancel ---------- */
   // @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST','DOCTOR')")

    @PostMapping("/cancel/{id}")
    public ResponseEntity<Void> cancel(
            @PathVariable("id") Long appointmentId,
            @Valid @RequestBody AppointmentCancelDTO body) {

        // copy path‑id into DTO (or rely on body if you pass it)
        body.setAppointmentId(appointmentId);

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        appointmentService.cancelAppointment(body, username);
        return ResponseEntity.noContent().build();
    }

    /* ---------- reschedule ---------- */
  //  @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST','DOCTOR')")
    @PostMapping("/reschedule/{id}")
    public ResponseEntity<AppointmentResponseDTO> reschedule(
            @PathVariable("id") Long appointmentId,
            @Valid @RequestBody AppointmentRescheduleDTO body) {

        body.setAppointmentId(appointmentId);
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        AppointmentResponseDTO dto = appointmentService.rescheduleAppointment(body, username);
        return ResponseEntity.ok(dto);
    }

    /* ---------- flexible search ---------- */
    @GetMapping("/search")
    public ResponseEntity<List<AppointmentResponseDTO>> search(
            @RequestParam(required = false) Long doctorId,
            @RequestParam(required = false) Long patientId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate appointmentDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime endTime,
            @RequestParam(required = false) AppointmentStatus status,
            @RequestParam(required = false) String createdBy,
            @RequestParam(required = false) String cancelledBy,
            @RequestParam(required = false) String rescheduledBy) {

        List<AppointmentResponseDTO> results = appointmentService.searchAppointments(
                doctorId, patientId, appointmentDate, startTime, endTime,
                status, createdBy, cancelledBy, rescheduledBy);

        return ResponseEntity.ok(results);
    }
}
