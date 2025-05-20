package com.clinic.service.services;

import com.clinic.dto.DoctorSchedule.DoctorScheduleRequestDTO;
import com.clinic.dto.DoctorSchedule.DoctorScheduleResponseDTO;

import java.util.List;

public interface DoctorScheduleService {

    List<DoctorScheduleResponseDTO> createSchedules(DoctorScheduleRequestDTO requestDTO);

    List<DoctorScheduleResponseDTO> getSchedulesByDoctor(Long doctorId);

    List<DoctorScheduleResponseDTO> getSchedulesByDoctorAndDay(Long doctorId, String dayOfWeek);

    void deleteSchedule(Long scheduleId);
}
