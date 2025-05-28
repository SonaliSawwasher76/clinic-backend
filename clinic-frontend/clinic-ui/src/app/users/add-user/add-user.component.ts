import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { UserService, SignupRequestWrapper } from '../../services/user.service';
import { NgForOf, NgIf } from '@angular/common';

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
  showErrorToast = false;
  errorMessage = '';

  constructor(private fb: FormBuilder, private userService: UserService) {}

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
    const formValue = this.userForm.value;

    // Wrap user fields inside user property
    const userPayload = {
      email: formValue.email,
      password: formValue.password,
      role: formValue.role,
      firstName: formValue.firstName,
      lastName: formValue.lastName,
      dob: formValue.dob,
      contactNo: formValue.contactNo,
      gender: formValue.gender,
      address: formValue.address,
      workspaceId: Number(formValue.workspaceId),
    };

    let doctorPayload = null;
    if (this.isDoctorRole) {
      doctorPayload = {
        specialization: formValue.specialization,
        licenseNumber: formValue.licenseNumber,
        yearsOfExperience: Number(formValue.yearsOfExperience),
      };
    }

    const payload: SignupRequestWrapper = {
      user: userPayload,
      doctor: doctorPayload,
    };

    this.userService.createUser(payload).subscribe({
      next: (response) => {
        // Access the message from the JSON response
        this.showSuccessToast = true;
        this.errorMessage = response.message || 'User registered successfully';
        this.userForm.reset();
        this.isDoctorRole = false;

        setTimeout(() => {
          this.showSuccessToast = false;
        }, 3000);
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to create user';
        this.showErrorToast = true;
        setTimeout(() => {
          this.showErrorToast = false;
        }, 3000);
      }
    });
  } else {
    this.userForm.markAllAsTouched();
  }
}

}
