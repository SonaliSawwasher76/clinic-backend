package com.clinic.entity;

import com.clinic.entity.user.User;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "doctors")
@Data
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long doctorId;

    private String specialization;
    private String licenseNumber;
    private Integer yearsOfExperience;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
}
