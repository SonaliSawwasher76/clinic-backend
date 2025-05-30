import { Component, OnInit } from '@angular/core';
import { PatientService } from '../../services/patient.service';
import { MedicalHistoryService } from '../../services/medical-history.service';
import { NgFor, NgIf } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';

@Component({
  selector: 'app-patients-record',
  standalone:true,
  imports:[NgIf,NgFor,ReactiveFormsModule],
  templateUrl: './patients-record.component.html',
  styleUrls: ['./patients-record.component.css']
})
export class PatientsRecordComponent implements OnInit {
  patients: any[] = [];
  patientIdsWithHistory: Set<number> = new Set();
  selectedPatientHistory: any[] | null = null;
  selectedPatientId: number | null = null;

  showHistoryView = false;

    // Form and mode control
  historyForm: FormGroup;
  isEditMode = false;
  editingHistoryId: number | null = null;
  showForm = false;
  successMessage: string | null = null;
  errorMessage: string | null = null;

  constructor(
    private patientService: PatientService,
    private medicalHistoryService: MedicalHistoryService,
    private fb: FormBuilder
  ) {
    this.historyForm = this.fb.group({
      diseaseName: ['', Validators.required],
      diagnosisDate: ['', Validators.required],
      treatmentDetails: [''],
      notes: ['']
    });
  }

  ngOnInit(): void {
    this.loadPatients();
  }

  // loadPatients() {
  //   this.patientService.getAllPatients().subscribe(patients => {
  //     this.patients = patients;
  //     this.loadPatientsWithHistory();
  //   });
  // }

  loadPatients() {
  const workspaceId = localStorage.getItem('workspaceId');  // or get from a service if needed

  if (!workspaceId) {
    console.error("Workspace ID not found.");
    return;
  }

  this.patientService.getPatientsByWorkspace(Number(workspaceId)).subscribe(patients => {
    this.patients = patients;
    this.loadPatientsWithHistory();
  });
}


  loadPatientsWithHistory() {
    this.medicalHistoryService.getPatientsWithMedicalHistory().subscribe(ids => {
      this.patientIdsWithHistory = new Set(ids);
    });
  }

  hasMedicalHistory(patientId: number): boolean {
    return this.patientIdsWithHistory.has(patientId);
  }

  viewHistory(patient: any) {
  this.selectedPatientId = patient.id;

  this.medicalHistoryService.getByPatientId(patient.id).subscribe(history => {
    this.selectedPatientHistory = history;
     this.showHistoryView = true;
     this.showForm = false;
  });
}


updateHistory(patient: any) {
  this.showHistoryView = false;
  this.isEditMode = true;
  this.selectedPatientId = patient.id;

  this.medicalHistoryService.getByPatientId(patient.id).subscribe(history => {
    if (history.length > 0) {
      this.editingHistoryId = history[0].medicalHistoryId;

      this.historyForm.patchValue({
        diseaseName: history[0].diseaseName,
        diagnosisDate: history[0].diagnosisDate,
        treatmentDetails: history[0].treatmentDetails,
        notes: history[0].notes
      });

      this.showForm = true;
    } else {
      alert('No history record found to update.');
    }
  });
}



  addHistory(patient: any) {
    // implement add logic
    this.showHistoryView = false;
    this.showForm = true;
    this.isEditMode = false;
    this.editingHistoryId = null;
    this.selectedPatientId = patient.id;
    this.selectedPatientHistory = null;
    this.historyForm.reset();
  }

   goBackToPatients() {
    this.showHistoryView = false;
    this.selectedPatientHistory = null;
    this.selectedPatientId = null;
    this.showForm = false;

  }

// onSubmit() {
//   console.log("isEditMode:", this.isEditMode);
//   console.log("editingHistoryId:", this.editingHistoryId);

//   if (this.historyForm.invalid) {
//     this.historyForm.markAllAsTouched();
//     return;
//   }

//   const formData = this.historyForm.value;
//   formData.patientId = this.selectedPatientId;
  
//   if (this.isEditMode && this.editingHistoryId == null) {
//   alert("Cannot update. History ID is not set.");
//   return;
// }


//   if (this.isEditMode && this.editingHistoryId !== null) {
//     // ✅ Ensure you call update only when in edit mode and ID is defined
//     this.medicalHistoryService.updateMedicalHistory(this.editingHistoryId, formData).subscribe(() => {
//       alert('Medical history updated successfully.');
//       this.showForm = false;
//       this.editingHistoryId = null;
//       this.loadPatientsWithHistory();
//     });
//   } else {
//     // ✅ Add new history
//     this.medicalHistoryService.addMedicalHistory(formData).subscribe(() => {
//       alert('Medical history added successfully.');
//       this.showForm = false;
//       this.loadPatientsWithHistory();
//     });
//   }
// }



onSubmit() {
  if (this.historyForm.invalid) {
    this.historyForm.markAllAsTouched();
    this.errorMessage = "Please correct the errors in the form.";
    return;
  }

  const formData = this.historyForm.value;
  formData.patientId = this.selectedPatientId;

  if (this.isEditMode && this.editingHistoryId == null) {
    this.errorMessage = "Cannot update. History ID is not set.";
    return;
  }

  if (this.isEditMode && this.editingHistoryId !== null) {
    this.medicalHistoryService.updateMedicalHistory(this.editingHistoryId, formData).subscribe({
      next: () => {
        this.successMessage = "Medical history updated successfully.";
        this.errorMessage = null;
        this.resetFormAfterDelay();
      },
      error: () => {
        this.errorMessage = "Failed to update medical history.";
        this.successMessage = null;
      }
    });
  } else {
    this.medicalHistoryService.addMedicalHistory(formData).subscribe({
      next: () => {
        this.successMessage = "Medical history added successfully.";
        this.errorMessage = null;
        this.resetFormAfterDelay();
      },
      error: () => {
        this.errorMessage = "Failed to add medical history.";
        this.successMessage = null;
      }
    });
  }
}

resetFormAfterDelay() {
  setTimeout(() => {
    this.showForm = false;
    this.editingHistoryId = null;
    this.loadPatientsWithHistory();
    this.successMessage = null;
    this.errorMessage = null;
  }, 2000);
}



  cancelForm() {
    this.showForm = false;
    this.editingHistoryId = null;
    this.historyForm.reset();
    this.showHistoryView=false;
  }
}

