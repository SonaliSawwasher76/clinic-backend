package com.clinic.dto.Appointment;


import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import com.clinic.dto.Appointment.ServiceDTO;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class VisitRecordResponseDTO {
    private Long visitRecordId;
    private Long appointmentId;
    private String patientFullName;          // helpful for UI / PDF
    private String doctorFullName;
    private String symptoms;
    private String diagnosis;
    private String prescription;
    private String notes;
    private List<ServiceDTO> services;
    private BigDecimal totalAmount;
    private LocalDateTime visitDateTime;
    private LocalDateTime createdAt;
}
