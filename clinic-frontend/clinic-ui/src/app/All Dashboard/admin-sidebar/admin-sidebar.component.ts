import { NgIf } from '@angular/common';
import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';

type SectionKey = 'patientManagement' | 'appointmentManagement' | 'staffManagement' | 'billingInvoicing';

@Component({
  selector: 'app-admin-sidebar',
  imports: [RouterLink,NgIf,RouterLinkActive],
  standalone: true,
  templateUrl: './admin-sidebar.component.html',
  styleUrls: ['./admin-sidebar.component.css']
})
export class AdminSidebarComponent {
  isSectionOpen: Record<SectionKey, boolean> = {
    patientManagement: false,
    appointmentManagement: false,
    staffManagement: false,
    billingInvoicing: false
  };

   workspaceName: string = '';
   constructor() {
    this.workspaceName = localStorage.getItem('workspaceName') || 'Clinic Panel';
  }

  toggleSection(section: SectionKey) {
    this.isSectionOpen[section] = !this.isSectionOpen[section];
  }
}