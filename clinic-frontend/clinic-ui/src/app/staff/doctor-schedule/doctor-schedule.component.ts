import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { DoctorScheduleService, DoctorResponseDTO, ScheduleDTO } from '../../services/doctor-schedule.service';
import { NgFor, NgIf } from '@angular/common';
import { forkJoin } from 'rxjs';

@Component({
  selector: 'app-doctor-schedule',
  standalone: true,
  imports: [NgFor, NgIf],
  templateUrl: './doctor-schedule.component.html',
  styleUrls: ['./doctor-schedule.component.css']
})
export class DoctorScheduleComponent implements OnInit {
  doctors: DoctorResponseDTO[] = [];
  selectedDoctorId: number | null = null;
  schedules: ScheduleDTO[] = [];
  loadingSchedules = false;
  doctorSchedulesMap: { [doctorId: number]: ScheduleDTO[] } = {};
  viewMode = false;

  constructor(
    private doctorScheduleService: DoctorScheduleService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    const workspaceId = localStorage.getItem('workspaceId');
    if (workspaceId) {
      this.doctorScheduleService.getDoctorsByWorkspace(workspaceId).subscribe((doctors) => {
        const scheduleObservables = doctors.map(doc =>
          this.doctorScheduleService.getSchedulesByDoctor(doc.doctorId)
        );

        forkJoin(scheduleObservables).subscribe((allSchedules) => {
          this.doctors = doctors;
          this.doctorSchedulesMap = {};
          doctors.forEach((doc, idx) => {
            this.doctorSchedulesMap[doc.doctorId] = allSchedules[idx];
          });
        });
      });
    }
     this.loadDoctorsAndSchedules();
  }

  onViewSchedules(doctor: DoctorResponseDTO): void {
    this.selectedDoctorId = doctor.doctorId;
    this.viewMode = true;
    this.loadingSchedules = true;

    this.doctorScheduleService.getSchedulesByDoctor(doctor.doctorId).subscribe((schedules) => {
      this.schedules = schedules;
      this.doctorSchedulesMap[doctor.doctorId] = schedules; // sync state
      this.loadingSchedules = false;
      this.cdr.detectChanges();
    });
  }

  onDeleteSchedule(scheduleId: number): void {
  if (!this.selectedDoctorId) return;

  if (confirm('Are you sure you want to delete this schedule?')) {
    this.doctorScheduleService.deleteSchedule(scheduleId).subscribe(() => {
      // After delete, reload all data cleanly
      this.loadDoctorsAndSchedules();
    });
  }
}


loadDoctorsAndSchedules(): void {
  const workspaceId = localStorage.getItem('workspaceId');
  if (workspaceId) {
    this.doctorScheduleService.getDoctorsByWorkspace(workspaceId).subscribe((doctors) => {
      const schedulesObservables = doctors.map(doc =>
        this.doctorScheduleService.getSchedulesByDoctor(doc.doctorId)
      );

      forkJoin(schedulesObservables).subscribe((allSchedules) => {
        this.doctors = doctors;
        this.doctorSchedulesMap = {};
        doctors.forEach((doc, idx) => {
          this.doctorSchedulesMap[doc.doctorId] = allSchedules[idx];
        });

        // If viewing a doctor, refresh the selected schedules too
        if (this.selectedDoctorId) {
          this.schedules = this.doctorSchedulesMap[this.selectedDoctorId] || [];
          if (this.schedules.length === 0) {
            this.viewMode = false;
            this.selectedDoctorId = null;
          }
        }
      });
    });
  }
}


  onAddUpdateSchedules(doctor: DoctorResponseDTO): void {
    // Placeholder: Open form/modal
    console.log('Add/Update for doctor:', doctor);
  }

  getDoctorName(doctorId: number | null): string {
    const doc = this.doctors.find(d => d.doctorId === doctorId);
    return doc ? `${doc.firstName} ${doc.lastName}` : '';
  }

  closeView(): void {
    this.viewMode = false;
    this.selectedDoctorId = null;
    this.schedules = [];
  }

  hasSchedule(doctorId: number): boolean {
  const schedules = this.doctorSchedulesMap[doctorId];
  return schedules !== undefined && schedules.length > 0;
}

}
