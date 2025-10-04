// school-class.service.ts

import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { PageResponse } from '../../common-types';
import { ClassRequest, ClassResponse } from './class-types';

@Injectable({
  providedIn: 'root'
})
export class ClassService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = 'http://localhost:8080/api/academic/classes';

  createClass(request: ClassRequest): Observable<ClassResponse> {
    return this.http.post<ClassResponse>(this.apiUrl, request);
  }

  getClassById(id: number): Observable<ClassResponse> {
    return this.http.get<ClassResponse>(`${this.apiUrl}/${id}`);
  }

  getAllClasses(
    page: number = 0,
    size: number = 10,
    sortBy: string = 'className',
    sortDir: string = 'asc'
  ): Observable<PageResponse<ClassResponse>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sortBy', sortBy)
      .set('sortDir', sortDir);

    return this.http.get<PageResponse<ClassResponse>>(
      this.apiUrl,
      { params }
    );
  }

  getClassesBySession(
    sessionId: number,
    page: number = 0,
    size: number = 10
  ): Observable<PageResponse<ClassResponse>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<PageResponse<ClassResponse>>(
      `${this.apiUrl}/session/${sessionId}`,
      { params }
    );
  }

  getActiveClassesBySession(sessionId: number): Observable<ClassResponse[]> {
    return this.http.get<ClassResponse[]>(
      `${this.apiUrl}/session/${sessionId}/active`
    );
  }

  updateClass(
    id: number,
    request: ClassRequest
  ): Observable<ClassResponse> {
    return this.http.put<ClassResponse>(
      `${this.apiUrl}/${id}`,
      request
    );
  }

  activateClass(id: number): Observable<string> {
    return this.http.put<string>(`${this.apiUrl}/${id}/activate`, null);
  }

  deactivateClass(id: number): Observable<string> {
    return this.http.put<string>(`${this.apiUrl}/${id}/deactivate`, null);
  }

  deleteClass(id: number): Observable<string> {
    return this.http.delete<string>(`${this.apiUrl}/${id}`);
  }
}
