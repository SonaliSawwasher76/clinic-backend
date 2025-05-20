package com.clinic.dto.Appointment;

import lombok.*;

import java.math.BigDecimal;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ServiceDTO {
    private Long serviceId;
    private String name;
    private BigDecimal price;
    private String description;
}
