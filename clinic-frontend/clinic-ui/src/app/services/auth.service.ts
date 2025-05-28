import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  token:string;
 // accessToken: string;
  refreshToken: string;
  role: string;
  userId: number;
  message: string;
  firstName: string;
  workspaceName: string;
}

export interface RefreshTokenResponse {
  accessToken: string;
  refreshToken: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'http://localhost:8080/api/auth'; // Adjust base URL if needed

  constructor(private http: HttpClient) {}

  login(payload: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.apiUrl}/login`, payload);
  }

  // New refresh token method
  refreshAccessToken(refreshToken: string): Observable<RefreshTokenResponse> {
    return this.http.post<RefreshTokenResponse>(`${this.apiUrl}/refresh`, { refreshToken });
  }

  logout() {
    // Clear tokens and redirect logic here
   // localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('userId');
    localStorage.removeItem('firstName');
    localStorage.removeItem('role');
    localStorage.removeItem('workspaceName');
    localStorage.removeItem('token');
    // Add routing to login page or reload app as needed
    window.location.href = '/login'; // Adjust route if your login is elsewhere
  }
}
