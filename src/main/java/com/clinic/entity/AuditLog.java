package com.clinic.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

@Entity
@Table(name = "audit_log")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String action;        // e.g., CREATE_PATIENT
    private String module;        // e.g., Patient
    private String performedBy;   // hardcode or get from security context
    private LocalDateTime timestamp;

    @Column(columnDefinition = "TEXT")
    private String details; // Optional: store patient ID or name or DTO snapshot

    // Getters and Setters
}
