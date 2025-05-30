package com.clinic.dto.Auth.Staff;

import com.clinic.dto.Auth.UserDetailsResponseDTO;
import lombok.Data;

@Data
public class StaffUpdateRequest {
    private UserDetailsResponseDTO user;
    private Object doctor; // keep as Object for now, or DoctorDto if you implement it later
}
