import { Component, NgModule, OnInit } from '@angular/core';

import { StaffService } from '../../services/staff.service';
import { UserDetailsResponseDTO } from '../../models/user-details.dto';
import { NgFor, NgIf } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

declare var bootstrap: any;

@Component({
  selector: 'app-staff-list',
  imports:[NgIf,NgFor,FormsModule],
  templateUrl: './staff-list.component.html',
})
export class StaffListComponent implements OnInit {
  staffList: UserDetailsResponseDTO[] = [];

  // Filters
  searchText: string = '';
  selectedRole: string = 'all';

  selectedStaffId!: number;
  selectedStaffName!: string;

  // Pagination
  currentPage: number = 0;
  totalPages: number = 0;
  workspaceId: number = 8;

  constructor(private staffService: StaffService,private router:Router) {}

  ngOnInit(): void {
    this.fetchStaff();
  }

  fetchStaff(): void {
    this.staffService
      .getStaffList(this.workspaceId, this.selectedRole, this.searchText, this.currentPage)
      .subscribe((res) => {
        this.staffList = res.content;
        this.totalPages = res.totalPages;
      });
  }

  goToPage(page: number): void {
    this.currentPage = page;
    this.fetchStaff();
  }
   
  onFilterChange(): void {
  this.currentPage = 0;
  this.fetchStaff();
}

deleteStaff(userId: number): void {
  if (confirm('Are you sure you want to delete this staff member?')) {
    this.staffService.deleteStaff(userId).subscribe({
      next: () => {
        alert('Staff deleted successfully.');
        this.fetchStaff(); // reload table
      },
      error: (err) => {
        console.error(err);
        alert('Failed to delete staff.');
      }
    });
  }
}


openDeleteModal(userId: number, fullName: string): void {
    this.selectedStaffId = userId;
    this.selectedStaffName = fullName;

    const modalElement = document.getElementById('deleteStaffModal');
    const modal = new bootstrap.Modal(modalElement);
    modal.show();
  }

  confirmDelete(): void {
    this.staffService.deleteStaff(this.selectedStaffId).subscribe({
      next: () => {
        this.fetchStaff(); // refresh table
        const modalElement = document.getElementById('deleteStaffModal');
        const modal = bootstrap.Modal.getInstance(modalElement);
        modal.hide();
      },
      error: () => {
        alert('Delete failed.');
      }
    });
  }


  onEditStaff(id: number): void {
    this.router.navigate(['/dashboard/admin/staff/edit', id]);
  }

  
}
