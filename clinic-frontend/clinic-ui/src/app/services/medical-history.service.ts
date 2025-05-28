import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class MedicalHistoryService {

  private baseUrl = '/api/medical-history';

  constructor(private http: HttpClient) {}

  getPatientsWithMedicalHistory(): Observable<number[]> {
  return this.http.get<number[]>(`${this.baseUrl}/patients-with-history`);
}


  getByPatientId(patientId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/patient/${patientId}`);
  }

  addMedicalHistory(data: any): Observable<any> {
    return this.http.post<any>(this.baseUrl, data);
  }

  updateMedicalHistory(id: number, data: any): Observable<any> {
    return this.http.put<any>(`${this.baseUrl}/${id}`, data);
  }

  deleteMedicalHistory(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
