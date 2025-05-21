import { NgForOf, NgIf } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';

@Component({
  selector: 'app-add-user',
  imports: [ReactiveFormsModule, NgIf, NgForOf],
  templateUrl: './add-user.component.html',
  styleUrls: ['./add-user.component.css']
})
export class AddUserComponent implements OnInit {

  userForm!: FormGroup;
  roles: string[] = ['ADMIN', 'DOCTOR', 'RECEPTIONIST'];
  isDoctorRole = false;
  showSuccessToast = false;

  constructor(private fb: FormBuilder) {}

  ngOnInit(): void {
    this.userForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      role: ['', Validators.required],
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      dob: ['', Validators.required],
      contactNo: ['', Validators.required],
      gender: ['', Validators.required],
      address: ['', Validators.required],
      workspaceId: ['', Validators.required],

      // Doctor-specific fields
      specialization: [''],
      licenseNumber: [''],
      yearsOfExperience: ['']
    });
  }

  onRoleChange(): void {
    const selectedRole = this.userForm.get('role')?.value;
    this.isDoctorRole = selectedRole === 'DOCTOR';

    if (this.isDoctorRole) {
      this.userForm.get('specialization')?.setValidators(Validators.required);
      this.userForm.get('licenseNumber')?.setValidators(Validators.required);
      this.userForm.get('yearsOfExperience')?.setValidators([Validators.required, Validators.min(1)]);
    } else {
      this.userForm.get('specialization')?.clearValidators();
      this.userForm.get('licenseNumber')?.clearValidators();
      this.userForm.get('yearsOfExperience')?.clearValidators();
    }

    this.userForm.get('specialization')?.updateValueAndValidity();
    this.userForm.get('licenseNumber')?.updateValueAndValidity();
    this.userForm.get('yearsOfExperience')?.updateValueAndValidity();
  }

  onSubmit(): void {
    if (this.userForm.valid) {
      console.log('Form Submitted:', this.userForm.value);

      this.showSuccessToast = true;

      setTimeout(() => {
        this.showSuccessToast = false;
      }, 3000);

      // You can add backend API call here
    } else {
      console.warn('Form is invalid');
      this.userForm.markAllAsTouched(); // show errors
    }
  }
}
