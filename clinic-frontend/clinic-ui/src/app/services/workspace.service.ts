// import { Injectable } from '@angular/core';
// import { HttpClient } from '@angular/common/http';
// import { Observable } from 'rxjs';

// import { WorkspaceRequestDTO, WorkspaceResponseDTO } from '../models/workspace.model'

// @Injectable({
//   providedIn: 'root',
// })
// export class WorkspaceService {

//   private apiUrl = 'http://localhost:8080/api/workspaces';

//   constructor(private http: HttpClient) {}

//   createWorkspace(data: WorkspaceRequestDTO): Observable<WorkspaceResponseDTO> {
//     return this.http.post<WorkspaceResponseDTO>(this.apiUrl, data);
//   }
// }


import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { delay } from 'rxjs/operators';

import { WorkspaceRequestDTO, WorkspaceResponseDTO } from '../models/workspace.model';

@Injectable({
  providedIn: 'root',
})
export class WorkspaceService {
  createWorkspace(data: WorkspaceRequestDTO): Observable<WorkspaceResponseDTO> {
    console.log('Mock createWorkspace called with:', data);

    // Mock response simulating a saved workspace with generated ID
    const mockResponse: WorkspaceResponseDTO = {
      workspaceId: Math.floor(Math.random() * 1000), // random id
      ...data,
    };

    // Return mock response after 1 second delay to simulate network call
    return of(mockResponse).pipe(delay(1000));
  }
}

