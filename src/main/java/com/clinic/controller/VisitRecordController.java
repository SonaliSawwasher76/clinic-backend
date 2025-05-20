package com.clinic.controller;

import com.clinic.dto.Appointment.VisitRecordRequestDTO;
import com.clinic.dto.Appointment.VisitRecordResponseDTO;
import com.clinic.service.services.VisitRecordService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/visit-records")
public class VisitRecordController {

    private final VisitRecordService visitRecordService;

    public VisitRecordController(VisitRecordService visitRecordService) {
        this.visitRecordService = visitRecordService;
    }

    /* ---------- COMPLETE APPOINTMENT ---------- */
    @PostMapping("/complete/{appointmentId}")
    public ResponseEntity<VisitRecordResponseDTO> completeAppointment(
            @PathVariable Long appointmentId,
            @RequestBody VisitRecordRequestDTO requestDTO) {

        VisitRecordResponseDTO response =
                visitRecordService.completeAppointment(appointmentId, requestDTO);

        return ResponseEntity.status(201).body(response);
    }

    /* ---------- FLEXIBLE SEARCH ---------- */
    @GetMapping("/search")
    public ResponseEntity<List<VisitRecordResponseDTO>> search(
            @RequestParam(required = false) Long visitRecordId,
            @RequestParam(required = false) Long appointmentId,
            @RequestParam(required = false) Long patientId,
            @RequestParam(required = false) Long doctorId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime fromDateTime,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime toDateTime) {

        List<VisitRecordResponseDTO> results =
                visitRecordService.searchVisitRecords(
                        visitRecordId,
                        appointmentId,
                        patientId,
                        doctorId,
                        fromDateTime,
                        toDateTime
                );

        return ResponseEntity.ok(results);
    }
}
