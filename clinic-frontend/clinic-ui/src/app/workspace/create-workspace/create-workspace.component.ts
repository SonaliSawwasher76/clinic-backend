import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { WorkspaceService } from '../../services/workspace.service';
import { NgIf } from '@angular/common';
import { Router } from '@angular/router';



@Component({
  selector: 'app-create-workspace',
  imports: [ReactiveFormsModule, NgIf],
  templateUrl: './create-workspace.component.html',
  styleUrls: ['./create-workspace.component.css']
})
export class CreateWorkspaceComponent implements OnInit {
  workspaceForm!: FormGroup;
  successMessage = '';
  errorMessage = '';

  ngOnInit() {
    this.workspaceForm = this.fb.group({
      name: ['', Validators.required],
      address: ['', Validators.required],
      contactNumber: ['', [Validators.required, Validators.pattern('^[0-9]{10}$')]],
      email: ['', [Validators.required, Validators.email]],
    });
  }

  constructor(private fb: FormBuilder, private workspaceService: WorkspaceService, private router: Router) { }

 onSubmit() {
  this.successMessage = '';
  this.errorMessage = '';

  if (this.workspaceForm.invalid) {
    return;
  }

  const formData = this.workspaceForm.value;

  this.workspaceService.createWorkspace(formData).subscribe({
    next: (response) => {
      console.log('Workspace created:', response);
      this.successMessage = 'Workspace created successfully!';
      this.workspaceForm.reset();

      // ✅ Delay navigation to show success message
      setTimeout(() => {
        this.router.navigate(['/add-user']);
      }, 2000); // 2 seconds delay
    },
    error: (error) => {
      console.error('Error creating workspace:', error);
      if (error.error && typeof error.error === 'string') {
        this.errorMessage = error.error;
      } else if (error.error && error.error.message) {
        this.errorMessage = error.error.message;
      } else {
        this.errorMessage = 'Failed to create workspace. Please try again.';
      }
    }
  });
}

}
