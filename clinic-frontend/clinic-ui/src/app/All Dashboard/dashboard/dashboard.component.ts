import { Component, OnInit } from '@angular/core';
import { Router, RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  imports:[RouterOutlet]
})
export class DashboardComponent implements OnInit {
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
