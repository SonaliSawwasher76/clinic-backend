package com.clinic.dto.DoctorSchedule;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorScheduleResponseDTO {
    private Long id;
    private Long doctorId;
    private String dayOfWeek;
    private String startTime;
    private String endTime;
    private String lunchStartTime;
    private String lunchEndTime;
    private int appointmentDurationMinutes;
}
