package com.clinic.service.services;

import com.clinic.dto.Appointment.VisitRecordRequestDTO;
import com.clinic.dto.Appointment.VisitRecordResponseDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface VisitRecordService {

    /** Mark an appointment as completed and create its visit‑record. */
    VisitRecordResponseDTO completeAppointment(Long appointmentId,
                                               VisitRecordRequestDTO request);

    /** Flexible search – any parameter may be null and will be ignored. */
    List<VisitRecordResponseDTO> searchVisitRecords(Long visitRecordId,
                                                    Long appointmentId,
                                                    Long patientId,
                                                    Long doctorId,
                                                    LocalDateTime fromDateTime,
                                                    LocalDateTime toDateTime);

    int getAmountByVisitRecordId(Long visitRecordId);
}
