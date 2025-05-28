import { Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { NgClass, NgIf } from '@angular/common';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [ReactiveFormsModule, NgIf, NgClass],
  templateUrl: './login.component.html',
})
export class LoginComponent {
  loginForm: FormGroup;
  submitted = false;
  loginError: string = '';
  loginSuccess: boolean = false;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required],
    });
  }

  onSubmit(): void {
    this.submitted = true;
    this.loginError = '';

    if (this.loginForm.invalid) return;

    const { email, password } = this.loginForm.value;

    this.authService.login({ email, password }).subscribe({
      next: (res) => {
        localStorage.setItem('token',res.token);
        //localStorage.setItem('accessToken', res.accessToken);
        localStorage.setItem('role', res.role);
        localStorage.setItem('userId', res.userId.toString());
        localStorage.setItem('firstName', res.firstName);
        localStorage.setItem('workspaceName',res.workspaceName)
        localStorage.setItem('refreshToken', res.refreshToken); 

        this.loginSuccess = true;

        // Redirect to role-specific child dashboard
        if (res.role === 'ADMIN') {
          this.router.navigate(['/dashboard/admin']);
        } else if (res.role === 'DOCTOR') {
          this.router.navigate(['/dashboard/doctor']);
        } else if (res.role === 'RECEPTIONIST') {
          this.router.navigate(['/dashboard/receptionist']);
        } else {
          this.router.navigate(['/']);
        }
      },
      error: (err) => {
        this.loginError = err.error?.message || 'Invalid email or password';
      }
    });

  }
}
