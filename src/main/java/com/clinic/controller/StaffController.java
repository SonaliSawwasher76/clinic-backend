package com.clinic.controller;

import com.clinic.dto.Auth.Staff.StaffUpdateRequest;
import com.clinic.dto.Auth.UserDetailsResponseDTO;
import com.clinic.service.services.StaffService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/api/staff")
@RequiredArgsConstructor
public class StaffController {

    private final StaffService staffService;
    @GetMapping
    public Page<UserDetailsResponseDTO> getStaffList(
            @RequestParam Long workspaceId,
            @RequestParam(defaultValue = "all") String role,
            @RequestParam(required = false) String searchText,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "userId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        System.out.println("SearchText In controller: " + searchText);
        return staffService.getStaffByFilters(workspaceId, role, searchText, pageable);
    }


//    @PutMapping("/{userId}")
//    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'RECEPTIONIST')")
//
//    public ResponseEntity<UserDetailsResponseDTO> updateStaff(
//            @PathVariable Long userId,
//            @RequestBody StaffUpdateRequest request) {
//
//        return ResponseEntity.ok(staffService.updateStaff(userId, request.getUser()));
//    }

    @PutMapping("/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'RECEPTIONIST')")
    public ResponseEntity<UserDetailsResponseDTO> updateStaff(
            @PathVariable Long userId,
            @RequestBody StaffUpdateRequest request) {

        UserDetailsResponseDTO userDto = request.getUser();

        if (request.getDoctor() != null) {
            // Convert doctor Object to Map to extract fields
            Map<String, Object> doctorMap = (Map<String, Object>) request.getDoctor();

            if (doctorMap.get("specialization") != null) {
                userDto.setSpecialization((String) doctorMap.get("specialization"));
            }
            if (doctorMap.get("licenseNumber") != null) {
                userDto.setLicenseNumber((String) doctorMap.get("licenseNumber"));
            }
            if (doctorMap.get("yearsOfExperience") != null) {
                // yearsOfExperience is Integer
                Integer years = null;
                Object val = doctorMap.get("yearsOfExperience");
                if (val instanceof Integer) {
                    years = (Integer) val;
                } else if (val instanceof Number) {
                    years = ((Number) val).intValue();
                }
                userDto.setYearsOfExperience(years);
            }
        }

        return ResponseEntity.ok(staffService.updateStaff(userId, userDto));
    }



    @DeleteMapping("/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'RECEPTIONIST')")

    public ResponseEntity<Void> deleteStaff(@PathVariable Long userId) {
        staffService.deleteStaff(userId);
        return ResponseEntity.noContent().build();
    }
}
