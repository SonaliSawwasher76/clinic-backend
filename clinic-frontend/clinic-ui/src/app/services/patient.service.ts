import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface PatientRequest {
  firstname: string;
  lastname: string;
  dob: string;         // ISO string expected by backend
  gender: string;
  contactNumber: string;
  email: string;
  address: string;
}

export interface PatientResponse extends PatientRequest {
  id: number;
}

@Injectable({
  providedIn: 'root'
})
export class PatientService {

  private apiUrl = 'http://localhost:8080/api/patients';  // Adjust if needed

  constructor(private http: HttpClient) { }

  createPatient(patient: PatientRequest): Observable<PatientResponse> {
    return this.http.post<PatientResponse>(`${this.apiUrl}`, patient);
  }

  getAllPatients(): Observable<PatientResponse[]> {
    return this.http.get<PatientResponse[]>(this.apiUrl);
  }

  searchPatients(query: string): Observable<PatientResponse[]> {
  return this.http.get<PatientResponse[]>(`${this.apiUrl}/search/general?query=${encodeURIComponent(query)}`);
}

getPatientById(id: number): Observable<PatientResponse> {
  return this.http.get<PatientResponse>(`${this.apiUrl}/${id}`);
}


  deletePatient(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  updatePatient(id: number, patient: PatientRequest): Observable<PatientResponse> {
    return this.http.put<PatientResponse>(`${this.apiUrl}/${id}`, patient);
  }
}
