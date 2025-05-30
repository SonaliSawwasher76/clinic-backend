import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

export interface DoctorResponseDTO {
  doctorId: number;
  firstName: string;
  lastName: string;
  email: string;
  gender:string;
  contactNo:string;
  specialization:string;
  licenseNumber:string;
  yearsOfExperience:string;
  address:string;
  workSpaceId:string;


}

export interface ScheduleDTO {

  id: number;
  doctorId:number;
  dayOfWeek: string;
  appointmentDurationMinutes: number;
  startTime: string;
  endTime: string;
  lunchStartTime: string;
  lunchEndTime: string;
}

@Injectable({
  providedIn: 'root'
})
export class DoctorScheduleService {
  private apiUrl = 'http://localhost:8080/api/doctors';
  private baseUrl = 'http://localhost:8080/api/doctor-schedules';
   // Adjust to your actual API base

  constructor(private http: HttpClient) {}

  getDoctorsByWorkspace(workspaceId: string): Observable<DoctorResponseDTO[]> {
    return this.http.get<DoctorResponseDTO[]>(`${this.apiUrl}/workspace/${workspaceId}`);
  }

  getSchedulesByDoctor(doctorId: number): Observable<ScheduleDTO[]> {
    return this.http.get<ScheduleDTO[]>(`${this.baseUrl}/doctor/${doctorId}`);
  }

  deleteSchedule(scheduleId: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${scheduleId}`);
  }

  // Add create/update methods later
}
