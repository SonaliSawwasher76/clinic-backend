package com.clinic.dto.Workspace;



import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkspaceResponseDTO {

    private Long workspaceId;
    private String name;
    private String address;
    private String contactNumber;
    private String email;
}

