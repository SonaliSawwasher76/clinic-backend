package com.clinic.controller;

import com.clinic.dto.DoctorSchedule.DoctorScheduleRequestDTO;
import com.clinic.dto.DoctorSchedule.DoctorScheduleResponseDTO;
import com.clinic.service.services.DoctorScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctor-schedules")
@RequiredArgsConstructor
@Tag(name = "Doctor Schedule", description = "APIs for managing doctor schedules")
public class DoctorScheduleController {

    private final DoctorScheduleService doctorScheduleService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create schedules for multiple days for a doctor")
    public ResponseEntity<List<DoctorScheduleResponseDTO>> createSchedules(@RequestBody DoctorScheduleRequestDTO requestDTO) {
        List<DoctorScheduleResponseDTO> responseDTOs = doctorScheduleService.createSchedules(requestDTO);
        return ResponseEntity.ok(responseDTOs);
    }

    @GetMapping("/doctor/{doctorId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<List<DoctorScheduleResponseDTO>> getSchedulesByDoctor(@PathVariable Long doctorId) {
        return ResponseEntity.ok(doctorScheduleService.getSchedulesByDoctor(doctorId));
    }

    @GetMapping("/doctor/{doctorId}/day/{day}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<List<DoctorScheduleResponseDTO>> getSchedulesByDoctorAndDay(
            @PathVariable Long doctorId,
            @PathVariable String day) {
        return ResponseEntity.ok(doctorScheduleService.getSchedulesByDoctorAndDay(doctorId, day));
    }

    @DeleteMapping("/{scheduleId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteSchedule(@PathVariable Long scheduleId) {
        doctorScheduleService.deleteSchedule(scheduleId);
        return ResponseEntity.ok("Schedule deleted successfully");
    }
}
