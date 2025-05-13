package com.clinic.entity.user;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "user_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_profile_id")
    private Long userProfileId;

    private String firstName;
    private String lastName;
    private String contactNo;
    private String gender;
    private String address;
    private LocalDate dob;

    // Removed bidirectional reference to User
}
