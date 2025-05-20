package com.clinic.entity.Appointment;

import com.clinic.entity.Doctor;
import com.clinic.entity.Patient;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import com.clinic.enums.AppointmentStatus;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "appointments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long appointmentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @Column(name = "appointment_date", nullable = false)
    private LocalDate appointmentDate;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(length = 500)
    private String reason;

    /* ---------- lifecycle / audit ---------- */
    @Enumerated(EnumType.STRING) @Column(nullable = false)
    private AppointmentStatus status = AppointmentStatus.BOOKED;

    /** JWT username that created the booking */
    @Column(nullable = false, updatable = false)
    private String createdBy;

    private LocalDateTime createdAt = LocalDateTime.now();

    /* cancellation */
    private String cancelledBy;                 // JWT username
    private LocalDateTime cancelledAt;
    private String cancellationReason;

    /* reschedule */
    private String rescheduledBy;               // JWT username
    private LocalDateTime rescheduledFrom;      // original start (date+time)
    private LocalDateTime rescheduledTo;        // new start (date+time)
    private String rescheduleReason;
}
