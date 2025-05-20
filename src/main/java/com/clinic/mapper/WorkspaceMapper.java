package com.clinic.mapper;

import com.clinic.dto.Workspace.WorkspaceRequestDTO;
import com.clinic.dto.Workspace.WorkspaceResponseDTO;
import com.clinic.entity.Workspace;
import org.springframework.stereotype.Component;

@Component
public class WorkspaceMapper {

    public Workspace toEntity(WorkspaceRequestDTO dto) {
        return Workspace.builder()
                .name(dto.getName())
                .address(dto.getAddress())
                .contactNumber(dto.getContactNumber())
                .email(dto.getEmail())
                .build();
    }

    public WorkspaceResponseDTO toDTO(Workspace entity) {
        return WorkspaceResponseDTO.builder()
                .workspaceId(entity.getWorkspaceId())
                .name(entity.getName())
                .address(entity.getAddress())
                .contactNumber(entity.getContactNumber())
                .email(entity.getEmail())
                .build();
    }
}
