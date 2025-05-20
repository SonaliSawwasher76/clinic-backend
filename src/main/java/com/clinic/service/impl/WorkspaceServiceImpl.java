package com.clinic.service.impl;

import com.clinic.dto.Workspace.WorkspaceRequestDTO;
import com.clinic.dto.Workspace.WorkspaceResponseDTO;
import com.clinic.entity.Workspace;
import com.clinic.exception.DuplicateResourceException;
import com.clinic.mapper.WorkspaceMapper;
import com.clinic.repository.WorkspaceRepository;
import com.clinic.service.services.WorkspaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WorkspaceServiceImpl implements WorkspaceService {

    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceMapper mapper;

    @Override
    public WorkspaceResponseDTO createWorkspace(WorkspaceRequestDTO requestDTO) {
        workspaceRepository.findByName(requestDTO.getName()).ifPresent(w -> {
            throw new DuplicateResourceException("Workspace name already exists");
        });

        workspaceRepository.findByEmail(requestDTO.getEmail()).ifPresent(w -> {
            throw new DuplicateResourceException("Email already exists");
        });

        Workspace saved = workspaceRepository.save(mapper.toEntity(requestDTO));
        return mapper.toDTO(saved);
    }
}
