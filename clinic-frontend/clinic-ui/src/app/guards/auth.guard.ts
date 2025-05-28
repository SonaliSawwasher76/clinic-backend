import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router, UrlTree } from '@angular/router';

@Injectable({
  providedIn: 'root',
})
export class AuthGuard implements CanActivate {
  constructor(private router: Router) {}

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean | UrlTree {
    const token = localStorage.getItem('token');
    const userRole = localStorage.getItem('role');

    if (!token) {
      // No token means not logged in
      return this.router.parseUrl('/login');
    }

    const allowedRoles = route.data?.['roles'] as string[] | undefined;

    // Debug logging
    console.log('AuthGuard -> Token:', token);
    console.log('AuthGuard -> Role from localStorage:', userRole);
    console.log('AuthGuard -> Allowed Roles:', allowedRoles);

    // If roles are specified on route, check for match
    if (allowedRoles && allowedRoles.length > 0) {
      if (!userRole || !allowedRoles.includes(userRole)) {
        return this.router.parseUrl('/login');
      }
    }

    return true; // Access granted
  }
}
