package com.clinic.mapper;

import com.clinic.dto.DoctorSchedule.DoctorScheduleRequestDTO;
import com.clinic.dto.DoctorSchedule.DoctorScheduleResponseDTO;
import com.clinic.entity.Appointment.DoctorSchedule;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Component
public class DoctorScheduleMapper {

    public DoctorSchedule toEntity(DoctorScheduleRequestDTO dto, DayOfWeek dayOfWeekEnum) {
        DoctorSchedule schedule = new DoctorSchedule();
        schedule.setDayOfWeek(dayOfWeekEnum);
        schedule.setStartTime(LocalTime.parse(dto.getStartTime()));
        schedule.setEndTime(LocalTime.parse(dto.getEndTime()));
        schedule.setLunchStartTime(LocalTime.parse(dto.getLunchStartTime()));
        schedule.setLunchEndTime(LocalTime.parse(dto.getLunchEndTime()));
        schedule.setAppointmentDurationMinutes(dto.getAppointmentDurationMinutes());
        return schedule;
    }

    public DoctorScheduleResponseDTO toResponseDTO(DoctorSchedule schedule) {
        return DoctorScheduleResponseDTO.builder()
                .id(schedule.getDoctorScheduleId())
                .doctorId(schedule.getDoctor().getDoctorId())
                .dayOfWeek(schedule.getDayOfWeek().name())
                .startTime(schedule.getStartTime().toString())
                .endTime(schedule.getEndTime().toString())
                .lunchStartTime(schedule.getLunchStartTime().toString())
                .lunchEndTime(schedule.getLunchEndTime().toString())
                .appointmentDurationMinutes(schedule.getAppointmentDurationMinutes())
                .build();
    }
}
