import { Component, OnInit } from '@angular/core';
import { Router, RouterOutlet } from '@angular/router';
import { AdminSidebarComponent } from "../admin-sidebar/admin-sidebar.component";

@Component({
  selector: 'app-admin-dashboard',
  templateUrl: './admin-dashboard.component.html',
  standalone: true,
  imports: [AdminSidebarComponent,RouterOutlet],
})
export class AdminDashboardComponent implements OnInit {
  firstName: string = '';

  constructor(private router: Router) {}

  ngOnInit(): void {
    this.firstName = localStorage.getItem('firstName') || '';
  }

  logout(): void {
    localStorage.clear();
    this.router.navigate(['/login']);
  }
}
