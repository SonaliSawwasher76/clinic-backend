package com.clinic.service.services;

import com.clinic.dto.Auth.UserDetailsResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StaffService {

    UserDetailsResponseDTO updateStaff(Long userId, UserDetailsResponseDTO updatedStaff);

    void deleteStaff(Long userId);
    Page<UserDetailsResponseDTO> getStaffByFilters(Long workspaceId, String role, String searchText, Pageable pageable);
}
