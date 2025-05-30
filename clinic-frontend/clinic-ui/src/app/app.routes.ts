import { Routes } from '@angular/router';
import { CreateWorkspaceComponent } from './workspace/create-workspace/create-workspace.component';
import { AddUserComponent } from './users/add-user/add-user.component';
import { LoginComponent } from './users/login/login.component';
import { StartupComponent } from './startup/startup.component';
import { DashboardComponent } from './All Dashboard/dashboard/dashboard.component';

import { AuthGuard } from './guards/auth.guard';
import { AdminDashboardComponent } from './All Dashboard/admin-dashboard/admin-dashboard.component';
import { DoctorDashboardComponent } from './All Dashboard/doctor-dashboard/doctor-dashboard.component';
import { ReceptionistDashboardComponent } from './All Dashboard/receptionist-dashboard/receptionist-dashboard.component';
import { AddPatientComponent } from './Patient/add-patient/add-patient.component';
import { PatientListComponent } from './Patient/patient-list/patient-list.component';
import { PatientsRecordComponent } from './Patient/patients-record/patients-record.component';
import { AddStaffComponent } from './staff/add-staff/add-staff.component';
import { StaffListComponent } from './staff/staff-list/staff-list.component';
import { DoctorScheduleComponent } from './staff/doctor-schedule/doctor-schedule.component';


export const routes: Routes = [
  {
    path: '',
    component: StartupComponent,
  },
  {
    path: 'create-workspace',
    component: CreateWorkspaceComponent,
  },
  {
    path: 'add-user',
    component: AddUserComponent,
  },
  {
    path: 'login',
    component: LoginComponent,
  },
  {
    path: 'dashboard',
    component: DashboardComponent,
    canActivate: [AuthGuard],
    children: [
      {
        path: 'admin',
        component: AdminDashboardComponent,
        canActivate: [AuthGuard],
        data: { roles: ['ADMIN'] },
        children: [
          {
            path: 'patients',
            component: PatientListComponent,
            canActivate: [AuthGuard],
            data: { roles: ['ADMIN'] }
          },
          {
            path: 'patients/add',
            component: AddPatientComponent,
            canActivate: [AuthGuard],
            data: { roles: ['ADMIN'] }
          },
          {
            path: 'patients/add/:id',
            component: AddPatientComponent,
            canActivate: [AuthGuard],
            data: { roles: ['ADMIN'] }
          },
          {
            path: 'patients/records',  // ✅ NEW ROUTE for Patient Records
            component: PatientsRecordComponent,
            canActivate: [AuthGuard],
            data: { roles: ['ADMIN'] }
          },

          {
            path: 'staff/addstaff',  
            component: AddStaffComponent,
            canActivate: [AuthGuard],
            data: { roles: ['ADMIN'] }
          },

          {
            path: 'staff/view',  
            component: StaffListComponent,
            canActivate: [AuthGuard],
            data: { roles: ['ADMIN'] }
          },

          { path: 'staff/edit/:id', 
            component: AddStaffComponent,
            canActivate: [AuthGuard],
            data: { roles: ['ADMIN'] } 
          },

          { path: 'staff/doctor_schedule', 
            component: DoctorScheduleComponent,
            canActivate: [AuthGuard],
            data: { roles: ['ADMIN'] } 
          },
        ]
      },
      {
        path: 'doctor',
        component: DoctorDashboardComponent,
        canActivate: [AuthGuard],
        data: { roles: ['DOCTOR'] }
      },
      {
        path: 'receptionist',
        component: ReceptionistDashboardComponent,
        canActivate: [AuthGuard],
        data: { roles: ['RECEPTIONIST'] }
      }
    ]
  },
  {
    path: '**',
    redirectTo: ''
  }
];
