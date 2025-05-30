import { Component, OnInit } from '@angular/core';
import { PatientService, PatientResponse } from '../../services/patient.service';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { NgFor } from '@angular/common';
import { debounceTime, Subject } from 'rxjs';

@Component({
  selector: 'app-patients-list',
  standalone: true,
  imports: [FormsModule, NgFor],
  templateUrl: './patient-list.component.html',
  styleUrls: ['./patient-list.component.css']
})
export class PatientListComponent implements OnInit {
  patients: PatientResponse[] = [];
  searchTerm: string = '';
  searchSubject = new Subject<string>();

  constructor(private patientService: PatientService, private router: Router) {}

  ngOnInit(): void {
    this.loadPatients();

    // Debounce search input
    this.searchSubject.pipe(debounceTime(300)).subscribe(query => {
      if (query.trim()) {
        this.patientService.searchPatients(query).subscribe(data => {
          this.patients = data;
        });
      } else {
        this.loadPatients();
      }
    });
  }

  loadPatients(): void {
    const workspaceId = Number(localStorage.getItem('workspaceId'));
    if (!workspaceId) {
      this.patients = [];
      return;
    }

    this.patientService.getPatientsByWorkspace(workspaceId).subscribe({
      next: (data) => {
        this.patients = data;
      },
      error: (err) => {
        console.error('Failed to load patients for workspace:', err);
        this.patients = [];
      }
    });
  }

  onSearchChange(): void {
    this.searchSubject.next(this.searchTerm);
  }

  deletePatient(id: number): void {
    if (confirm('Are you sure you want to delete this patient?')) {
      this.patientService.deletePatient(id).subscribe(() => {
        this.patients = this.patients.filter(p => p.id !== id);
      });
    }
  }

  onEditPatient(id: number): void {
    this.router.navigate(['/dashboard/admin/patients/add', id]);
  }
}
