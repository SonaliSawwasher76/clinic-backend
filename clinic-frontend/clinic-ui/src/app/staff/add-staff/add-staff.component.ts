import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { SignupRequestWrapper, UserService } from '../../services/user.service';
import { NgIf, NgForOf } from '@angular/common';
import { Router, ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-add-staff',
  standalone: true,
  imports: [ReactiveFormsModule, NgIf, NgForOf],
  templateUrl: './add-staff.component.html',
  styleUrl: './add-staff.component.css'
})
export class AddStaffComponent implements OnInit {

  userForm!: FormGroup;
  roles: string[] = ['ADMIN','DOCTOR', 'RECEPTIONIST'];
  isDoctorRole = false;
  showSuccessToast = false;
  showErrorToast = false;
  errorMessage = '';
  isEditMode = false;
  userIdToEdit?: number;

  constructor(
    private fb: FormBuilder,
    private userService: UserService,
    private router: Router,
    private route: ActivatedRoute,
  ) {}

  ngOnInit(): void {
    this.userForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.minLength(6)]],  // not required in edit
      role: ['', Validators.required],
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      dob: ['', Validators.required],
      contactNo: ['', Validators.required],
      gender: ['', Validators.required],
      address: ['', Validators.required],
      specialization: [''],
      licenseNumber: [''],
      yearsOfExperience: ['']
    });

    this.route.paramMap.subscribe(params => {
      const id = params.get('id');
      if (id) {
        this.isEditMode = true;
        this.userIdToEdit = +id;
        this.loadUserForEdit(this.userIdToEdit);
      } else {
        this.userForm.get('password')?.setValidators([Validators.required, Validators.minLength(6)]);
      }
    });
  }

  loadUserForEdit(userId: number): void {
    this.userService.getUserDetailsById(userId).subscribe(userData => {
      this.userForm.patchValue({
        email: userData.email,
        role: userData.role,
        firstName: userData.firstName,
        lastName: userData.lastName,
        dob: userData.dob,
        contactNo: userData.contactNo,
        gender: userData.gender,
        address: userData.address,
        specialization: userData.specialization,
        licenseNumber: userData.licenseNumber,
        yearsOfExperience: userData.yearsOfExperience,
      });

      this.isDoctorRole = userData.role === 'DOCTOR';
      this.onRoleChange();
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
      const workspaceId = Number(localStorage.getItem('workspaceId'));
      if (!workspaceId) {
        this.showToast(false, 'Workspace ID not found. Please login again.');
        return;
      }

      const userPayload: any = {
        email: formValue.email,
        role: formValue.role,
        firstName: formValue.firstName,
        lastName: formValue.lastName,
        dob: formValue.dob,
        contactNo: formValue.contactNo,
        gender: formValue.gender,
        address: formValue.address,
        workspaceId: workspaceId,
      };

      if (!this.isEditMode) {
        userPayload.password = formValue.password;
      } else if (formValue.password?.trim()) {
        userPayload.password = formValue.password; // only send if not blank
      }

      const doctorPayload = this.isDoctorRole
        ? {
            specialization: formValue.specialization,
            licenseNumber: formValue.licenseNumber,
            yearsOfExperience: Number(formValue.yearsOfExperience),
          }
        : null;

      const payload: SignupRequestWrapper = {
        user: userPayload,
        doctor: doctorPayload
      };

      console.log('Final payload being sent to backend:', payload);


      const request$ = this.isEditMode && this.userIdToEdit
        ? this.userService.updateUser(this.userIdToEdit, payload)
        : this.userService.createUser(payload);

      request$.subscribe({
        next: (res) => {
          this.showToast(true, res.message || 'User saved successfully');
          this.router.navigate(['/dashboard/admin/staff/view']);
        },
        error: (err) => {
          this.showToast(false, err.error?.message || 'Something went wrong');
        }
      });
    } else {
      this.userForm.markAllAsTouched();
    }
  }

  private showToast(success: boolean, message: string): void {
    this.errorMessage = message;
    if (success) {
      this.showSuccessToast = true;
      setTimeout(() => (this.showSuccessToast = false), 3000);
    } else {
      this.showErrorToast = true;
      setTimeout(() => (this.showErrorToast = false), 3000);
    }
  }
}
