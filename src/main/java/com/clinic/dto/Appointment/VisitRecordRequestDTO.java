package com.clinic.dto.Appointment;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class VisitRecordRequestDTO {
    private Long appointmentId;              // which appointment this finalises
    private String symptoms;
    private String diagnosis;
    private String prescription;
    private String notes;
    private List<Long> serviceIds;           // IDs of services ticked
    private LocalDateTime visitDateTime;     // when patient actually came
}
