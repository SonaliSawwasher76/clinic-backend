import { Routes } from '@angular/router';
import { CreateWorkspaceComponent } from './workspace/create-workspace/create-workspace.component';
import { AddUserComponent } from './users/add-user/add-user.component';


export const routes: Routes = [
    {
        path: '',
        redirectTo: 'create-workspace',
        pathMatch: 'full'
      },
      {
        path: 'create-workspace',
        component:CreateWorkspaceComponent
        
    },
    {
      path: 'add-user',
      component: AddUserComponent,
    },
];
