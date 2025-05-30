export interface UserDetailsResponseDTO {
  userId: number;
  email: string;
  role: string;
  workspaceId: number;
  firstName: string;
  lastName: string;
  dob: string;
  contactNo: string;
  gender: string;
  address: string;
  specialization?: string;
  licenseNumber?: string;
  yearsOfExperience?: number;
}
