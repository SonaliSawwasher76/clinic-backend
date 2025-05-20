package com.clinic.entity.billing;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "services")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Service {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long serviceId;

    @Column(nullable = false, unique = true)
    private String name;

    /** Base price in smallest currency unit (e.g., paise, cents) */

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    /** Optional longer description shown on invoices/ UI */
    @Column(columnDefinition = "TEXT")
    private String description;
}
