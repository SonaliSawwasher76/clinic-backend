package com.clinic.entity.Appointment;

import com.clinic.entity.billing.Service;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "visit_records")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VisitRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long visitRecordId;

    /* ---------- Links ---------- */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id", nullable = false, unique = true)
    private Appointment appointment;

    /* ---------- Clinical data ---------- */
    @Column(length = 500)
    private String symptoms;

    @Column(length = 500)
    private String diagnosis;

    @Column(columnDefinition = "TEXT")
    private String prescription;

    @Column(columnDefinition = "TEXT")
    private String notes;

    /* ---------- Services received ---------- */
    @ManyToMany
    @JoinTable(
            name = "visit_services",
            joinColumns = @JoinColumn(name = "visit_record_id"),
            inverseJoinColumns = @JoinColumn(name = "service_id")
    )
    private List<Service> services;   // full Service objects

    /* ---------- Billing ---------- */
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    /* ---------- Meta ---------- */
    @Column(nullable = false)
    private LocalDateTime visitDateTime;

    @Column(nullable = false)

    private LocalDateTime createdAt;
}
