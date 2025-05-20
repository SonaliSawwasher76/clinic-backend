package com.clinic.entity;

import com.clinic.entity.user.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "doctors")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long doctorId;

    private String specialization;
    private String licenseNumber;
    private Integer yearsOfExperience;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
