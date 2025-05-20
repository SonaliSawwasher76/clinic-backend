package com.clinic.service.services;


import com.clinic.dto.Workspace.WorkspaceRequestDTO;
import com.clinic.dto.Workspace.WorkspaceResponseDTO;

public interface WorkspaceService {
    WorkspaceResponseDTO createWorkspace(WorkspaceRequestDTO requestDTO);
}

