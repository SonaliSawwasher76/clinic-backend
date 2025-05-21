export interface WorkspaceRequestDTO {
    name: string;
    address: string;
    contactNumber: string;
    email: string;
  }
  
  export interface WorkspaceResponseDTO {
    workspaceId: number;
    name: string;
    address: string;
    contactNumber: string;
    email: string;
  }