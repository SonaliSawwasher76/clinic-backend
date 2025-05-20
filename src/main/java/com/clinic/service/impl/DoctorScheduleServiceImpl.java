package com.clinic.service.impl;

import com.clinic.dto.DoctorSchedule.DoctorScheduleRequestDTO;
import com.clinic.dto.DoctorSchedule.DoctorScheduleResponseDTO;
import com.clinic.entity.Appointment.DoctorSchedule;
import com.clinic.entity.Doctor;
import com.clinic.exception.InvalidInputException;
import com.clinic.mapper.DoctorScheduleMapper;
import com.clinic.repository.DoctorRepository;
import com.clinic.repository.DoctorScheduleRepository;
import com.clinic.service.services.DoctorScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DoctorScheduleServiceImpl implements DoctorScheduleService {

    private final DoctorScheduleRepository scheduleRepository;
    private final DoctorRepository doctorRepository;
    private final DoctorScheduleMapper mapper;

    @Override
    public List<DoctorScheduleResponseDTO> createSchedules(DoctorScheduleRequestDTO dto) {

        Doctor doctor = doctorRepository.findById(dto.getDoctorId())
                .orElseThrow(() -> new InvalidInputException("Doctor with ID " + dto.getDoctorId() + " not found"));

        // Parse times from strings
        LocalTime startTime = parseTime(dto.getStartTime(), "startTime");
        LocalTime endTime = parseTime(dto.getEndTime(), "endTime");
        LocalTime lunchStart = parseTime(dto.getLunchStartTime(), "lunchStartTime");
        LocalTime lunchEnd = parseTime(dto.getLunchEndTime(), "lunchEndTime");

        // Business Validations
        if (startTime.isAfter(endTime) || startTime.equals(endTime)) {
            throw new InvalidInputException("Start time must be before end time");
        }

        if (lunchStart.isBefore(startTime) || lunchEnd.isAfter(endTime) || lunchStart.isAfter(lunchEnd)) {
            throw new InvalidInputException("Lunch break must be within working hours and lunch start must be before lunch end");
        }

        if (dto.getAppointmentDurationMinutes() <= 0) {
            throw new InvalidInputException("Appointment duration must be positive");
        }

        List<DoctorScheduleResponseDTO> responseList = new ArrayList<>();

        for (String dayStr : dto.getDaysOfWeek()) {
            DayOfWeek day;
            try {
                day = DayOfWeek.valueOf(dayStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new InvalidInputException("Invalid day of week: " + dayStr);
            }

            DoctorSchedule schedule = DoctorSchedule.builder()
                    .doctor(doctor)
                    .dayOfWeek(day)
                    .startTime(startTime)
                    .endTime(endTime)
                    .lunchStartTime(lunchStart)
                    .lunchEndTime(lunchEnd)
                    .appointmentDurationMinutes(dto.getAppointmentDurationMinutes())
                    .build();

            schedule = scheduleRepository.save(schedule);
            responseList.add(mapper.toResponseDTO(schedule));
        }

        return responseList;
    }

    private LocalTime parseTime(String timeStr, String fieldName) {
        try {
            return LocalTime.parse(timeStr);
        } catch (Exception e) {
            throw new InvalidInputException("Invalid time format for " + fieldName + ": " + timeStr + ". Expected HH:mm");
        }
    }
    @Override
    public List<DoctorScheduleResponseDTO> getSchedulesByDoctor(Long doctorId) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found"));

        List<DoctorSchedule> schedules = scheduleRepository.findByDoctor(doctor);
        List<DoctorScheduleResponseDTO> responseList = new ArrayList<>();

        for (DoctorSchedule schedule : schedules) {
            responseList.add(mapper.toResponseDTO(schedule));
        }
        return responseList;
    }

    @Override
    public List<DoctorScheduleResponseDTO> getSchedulesByDoctorAndDay(Long doctorId, String dayOfWeek) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found"));

        DayOfWeek day = DayOfWeek.valueOf(dayOfWeek.toUpperCase());
        List<DoctorSchedule> schedules = scheduleRepository.findByDoctorAndDayOfWeek(doctor, day);

        List<DoctorScheduleResponseDTO> responseList = new ArrayList<>();
        for (DoctorSchedule schedule : schedules) {
            responseList.add(mapper.toResponseDTO(schedule));
        }
        return responseList;
    }

    @Override
    public void deleteSchedule(Long scheduleId) {
        scheduleRepository.findById(scheduleId).
                orElseThrow(() -> new IllegalArgumentException("Schedule not found"));


        scheduleRepository.deleteById(scheduleId);
    }
}
