package com.clinic.entity.Appointment;

import com.clinic.entity.Doctor;
import jakarta.persistence.*;
import lombok.*;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Entity
@Table(name = "doctor_schedules")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long doctorScheduleId;

    // Link to Doctor entity
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    // Day of week, e.g., MONDAY, TUESDAY ...
    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", nullable = false)
    private DayOfWeek dayOfWeek;

    // Work start time, e.g., 10:00 AM
    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    // Work end time, e.g., 5:00 PM
    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    // Lunch break start, e.g., 1:00 PM
    @Column(name = "lunch_start_time", nullable = false)
    private LocalTime lunchStartTime;

    // Lunch break end, e.g., 2:00 PM
    @Column(name = "lunch_end_time", nullable = false)
    private LocalTime lunchEndTime;

    // Appointment slot duration in minutes, e.g., 30
    @Column(name = "appointment_duration_minutes", nullable = false)
    private int appointmentDurationMinutes;
}
