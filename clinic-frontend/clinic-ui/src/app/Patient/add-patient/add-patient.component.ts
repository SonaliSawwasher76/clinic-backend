import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';

import { Router, ActivatedRoute } from '@angular/router';
import { PatientService, PatientRequest, PatientResponse } from '../../services/patient.service';
import { NgIf } from '@angular/common';

@Component({
  selector: 'app-add-patient',
  imports: [ReactiveFormsModule, NgIf],
  templateUrl: './add-patient.component.html',
  styleUrls: ['./add-patient.component.css']
})
export class AddPatientComponent implements OnInit {
  patientForm: FormGroup;
  isSubmitting = false;
  errorMessage = '';
  editMode = false;
  patientId: number | null = null;

  constructor(
    private fb: FormBuilder,
    private patientService: PatientService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    this.patientForm = this.fb.group({
      firstname: ['', [Validators.required, Validators.minLength(2)]],
      lastname: ['', [Validators.required, Validators.minLength(2)]],
      dob: ['', Validators.required],
      gender: ['', Validators.required],
      contactNumber: ['', [Validators.required, Validators.pattern(/^[0-9]{10}$/)]],
      email: ['', [Validators.required, Validators.email]],
      address: ['']
    });
  }

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const idParam = params.get('id');
      if (idParam) {
        this.editMode = true;
        this.patientId = +idParam;
        this.loadPatientData(this.patientId);
      }
    });
  }

  loadPatientData(id: number) {
    // Assuming you have getPatientById in service; add if not
    this.patientService.getPatientById(id).subscribe({
      next: (patient) => {
        // Patch form with existing patient data
        this.patientForm.patchValue({
          firstname: patient.firstname,
          lastname: patient.lastname,
          dob: patient.dob,
          gender: patient.gender,
          contactNumber: patient.contactNumber,
          email: patient.email,
          address: patient.address
        });
      },
      error: (err) => {
        this.errorMessage = 'Failed to load patient data.';
        console.error('Error loading patient:', err);
      }
    });
  }

  onSubmit() {
    if (this.patientForm.invalid) {
      this.patientForm.markAllAsTouched();
      return;
    }

    this.isSubmitting = true;

    const patientData: PatientRequest = this.patientForm.value;

    if (this.editMode && this.patientId !== null) {
      this.patientService.updatePatient(this.patientId, patientData).subscribe({
        next: () => {
          this.isSubmitting = false;
          this.router.navigate(['/dashboard/admin/patients']);
        },
        error: (err) => {
          this.isSubmitting = false;
          this.errorMessage = err.error?.message || 'Failed to update patient. Try again.';
          console.error('Error updating patient:', err);
        }
      });
    } else {
      this.patientService.createPatient(patientData).subscribe({
        next: () => {
          this.isSubmitting = false;
          this.router.navigate(['/dashboard/admin/patients']);
        },
        error: (err) => {
          this.isSubmitting = false;
          this.errorMessage = err.error?.message || 'Failed to create patient. Try again.';
          console.error('Error creating patient:', err);
        }
      });
    }
  }
}
