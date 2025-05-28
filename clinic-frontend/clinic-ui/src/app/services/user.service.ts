import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface UserCreateRequest {
  email: string;
  password: string;
  role: string;
  firstName: string;
  lastName: string;
  dob: string; // or Date, depending on backend
  contactNo: string;
  gender: string;
  address: string;
  workspaceId: number;
}

export interface DoctorCreateRequest {
  specialization: string;
  licenseNumber: string;
  yearsOfExperience: number;
}

export interface SignupRequestWrapper {
  user: UserCreateRequest;
  doctor?: DoctorCreateRequest | null;
}

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private baseUrl = 'http://localhost:8080/api/auth';

  constructor(private http: HttpClient) {}

  createUser(payload: SignupRequestWrapper): Observable<any> {
    return this.http.post(`${this.baseUrl}/signup`, payload);
  }
}
