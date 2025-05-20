package com.clinic.dto.DoctorSchedule;

import jakarta.validation.constraints.*;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorScheduleRequestDTO {

    @NotNull(message = "Doctor ID cannot be null")
    private Long doctorId;

    @NotEmpty(message = "Days of week list cannot be empty")
    private List<@Pattern(regexp = "MONDAY|TUESDAY|WEDNESDAY|THURSDAY|FRIDAY|SATURDAY|SUNDAY",
            message = "Day must be a valid day of the week") String> daysOfWeek;

    @NotBlank(message = "Start time cannot be blank")
    @Pattern(regexp = "^([01]?\\d|2[0-3]):[0-5]\\d$", message = "Start time must be in HH:mm format")
    private String startTime;

    @NotBlank(message = "End time cannot be blank")
    @Pattern(regexp = "^([01]?\\d|2[0-3]):[0-5]\\d$", message = "End time must be in HH:mm format")
    private String endTime;

    @NotBlank(message = "Lunch start time cannot be blank")
    @Pattern(regexp = "^([01]?\\d|2[0-3]):[0-5]\\d$", message = "Lunch start time must be in HH:mm format")
    private String lunchStartTime;

    @NotBlank(message = "Lunch end time cannot be blank")
    @Pattern(regexp = "^([01]?\\d|2[0-3]):[0-5]\\d$", message = "Lunch end time must be in HH:mm format")
    private String lunchEndTime;

    @Min(value = 1, message = "Appointment duration must be at least 1 minute")
    private int appointmentDurationMinutes;
}
