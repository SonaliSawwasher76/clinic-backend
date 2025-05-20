package com.clinic.controller;

import com.clinic.dto.Workspace.WorkspaceRequestDTO;
import com.clinic.dto.Workspace.WorkspaceResponseDTO;
import com.clinic.service.services.WorkspaceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/workspaces")
@RequiredArgsConstructor
public class WorkspaceController {

    private final WorkspaceService workspaceService;

    @PostMapping
    public ResponseEntity<WorkspaceResponseDTO> createWorkspace(@Valid @RequestBody WorkspaceRequestDTO requestDTO) {
        return ResponseEntity.ok(workspaceService.createWorkspace(requestDTO));
    }
}
