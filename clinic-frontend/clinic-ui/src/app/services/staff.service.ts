import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { UserDetailsResponseDTO } from '../models/user-details.dto'

@Injectable({
  providedIn: 'root',
})
export class StaffService {
  private baseUrl = 'http://localhost:8080/api/staff';

  constructor(private http: HttpClient) { }

  getStaffList(
    workspaceId: number,
    role: string,
    searchText: string,
    page: number = 0,
    size: number = 10,
    sortBy: string = 'userId',
    sortDir: string = 'asc'
  ): Observable<any> {
    let params = new HttpParams()
      .set('workspaceId', workspaceId)
      .set('role', role)
      .set('searchText', searchText)
      .set('page', page)
      .set('size', size)
      .set('sortBy', sortBy)
      .set('sortDir', sortDir);

    return this.http.get(this.baseUrl, { params });
  }


  deleteStaff(userId: number) {
    return this.http.delete(`${this.baseUrl}/${userId}`);
  }

 

}
