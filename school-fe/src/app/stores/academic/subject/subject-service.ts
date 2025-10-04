// subject-service.ts

import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { SubjectRequest, SubjectResponse } from './subject-types';
import { PageResponse } from '../../common-types';

@Injectable({
  providedIn: 'root'
})
export class SubjectService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = 'http://localhost:8080/api/academic/subjects';

  createSubject(request: SubjectRequest): Observable<SubjectResponse> {
    return this.http.post<SubjectResponse>(this.apiUrl, request);
  }

  getSubjectById(id: number): Observable<SubjectResponse> {
    return this.http.get<SubjectResponse>(`${this.apiUrl}/${id}`);
  }

  getSubjectByName(subjectName: string): Observable<SubjectResponse> {
    return this.http.get<SubjectResponse>(`${this.apiUrl}/name/${subjectName}`);
  }

  getSubjectByCode(subjectCode: string): Observable<SubjectResponse> {
    return this.http.get<SubjectResponse>(`${this.apiUrl}/code/${subjectCode}`);
  }

  getAllSubjects(
    page: number = 0,
    size: number = 10,
    sortBy: string = 'subjectName',
    sortDir: string = 'asc'
  ): Observable<PageResponse<SubjectResponse>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sortBy', sortBy)
      .set('sortDir', sortDir);

    return this.http.get<PageResponse<SubjectResponse>>(this.apiUrl, { params });
  }

  getActiveSubjects(): Observable<SubjectResponse[]> {
    return this.http.get<SubjectResponse[]>(`${this.apiUrl}/active`);
  }

  getSubjectsByStatus(
    isActive: boolean,
    page: number = 0,
    size: number = 10
  ): Observable<PageResponse<SubjectResponse>> {
    const params = new HttpParams()
      .set('isActive', isActive.toString())
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<PageResponse<SubjectResponse>>(`${this.apiUrl}/status`, { params });
  }

  updateSubject(id: number, request: SubjectRequest): Observable<SubjectResponse> {
    return this.http.put<SubjectResponse>(`${this.apiUrl}/${id}`, request);
  }

  activateSubject(id: number): Observable<string> {
    return this.http.put<string>(`${this.apiUrl}/${id}/activate`, null);
  }

  deactivateSubject(id: number): Observable<string> {
    return this.http.put<string>(`${this.apiUrl}/${id}/deactivate`, null);
  }

  deleteSubject(id: number): Observable<string> {
    return this.http.delete<string>(`${this.apiUrl}/${id}`);
  }
}
