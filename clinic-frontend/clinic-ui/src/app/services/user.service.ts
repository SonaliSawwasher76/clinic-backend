import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface UserCreateRequest {
  email: string;
  password: string;
  role: string;
  firstName: string;
  lastName: string;
  dob: string;
  contactNo: string;
  gender: string;
  address: string;
  workspaceId: number;
}

export interface UserUpdateRequest {
  email: string;
  password?: string;
  role: string;
  firstName: string;
  lastName: string;
  dob: string;
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
  user: UserCreateRequest | UserUpdateRequest;
  doctor?: DoctorCreateRequest | null;
}

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private baseUrl = 'http://localhost:8080/api/auth';
  private apiUrl = 'http://localhost:8080/api/staff';

  constructor(private http: HttpClient) {}

  createUser(payload: SignupRequestWrapper): Observable<any> {
    return this.http.post(`${this.baseUrl}/signup`, payload);
  }

  getUserDetailsById(id: number): Observable<any> {
    return this.http.get(`${this.baseUrl}/users/${id}`);
  }

  updateUser(id: number, payload: SignupRequestWrapper): Observable<any> {
    console.log('createUser payload:', payload);
    return this.http.put(`${this.apiUrl}/${id}`, payload);
  }
}
