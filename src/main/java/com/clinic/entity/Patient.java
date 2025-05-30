package com.clinic.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstname;

    private String lastname;

    private String gender;

    private LocalDate dob;

    private String contactNumber;

    private String email;

    private String address;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace_id")  // This must match your DB column name
    private Workspace workspace;

}
