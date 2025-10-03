// academic-session.service.ts

import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  AcademicSessionRequest,
  AcademicSessionResponse,
  SessionStatus
} from './academic-session-types';
import { ApiResponse, PageResponse } from '../../common-types';


@Injectable({
  providedIn: 'root'
})
export class AcademicSessionService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = 'http://localhost:8080/api/academic/sessions';

  createSession(request: AcademicSessionRequest): Observable<AcademicSessionResponse> {
    return this.http.post<AcademicSessionResponse>(this.apiUrl, request);
  }

  getSessionById(id: number): Observable<AcademicSessionResponse> {
    return this.http.get<AcademicSessionResponse>(`${this.apiUrl}/${id}`);
  }

  getSessionByName(sessionName: string): Observable<AcademicSessionResponse> {
    return this.http.get<AcademicSessionResponse>(
      `${this.apiUrl}/name/${sessionName}`
    );
  }

  getAllSessions(
    page: number = 0,
    size: number = 10,
    sortBy: string = 'sessionName',
    sortDir: string = 'asc'
  ): Observable<PageResponse<AcademicSessionResponse>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sortBy', sortBy)
      .set('sortDir', sortDir);

    return this.http.get<PageResponse<AcademicSessionResponse>>(
      this.apiUrl,
      { params }
    );
  }

  getSessionsByStatus(
    status: SessionStatus,
    page: number = 0,
    size: number = 10
  ): Observable<PageResponse<AcademicSessionResponse>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<PageResponse<AcademicSessionResponse>>(
      `${this.apiUrl}/status/${status}`,
      { params }
    );
  }

  getActiveSession(): Observable<AcademicSessionResponse> {
    return this.http.get<AcademicSessionResponse>(`${this.apiUrl}/active`);
  }

  getUpcomingSession(): Observable<AcademicSessionResponse> {
    return this.http.get<AcademicSessionResponse>(`${this.apiUrl}/upcoming`);
  }

  updateSession(
    id: number,
    request: AcademicSessionRequest
  ): Observable<AcademicSessionResponse> {
    return this.http.put<AcademicSessionResponse>(
      `${this.apiUrl}/${id}`,
      request
    );
  }

  activateSession(id: number): Observable<string> {
    return this.http.put<string>(`${this.apiUrl}/${id}/activate`, null);
  }

  deactivateSession(id: number): Observable<string> {
    return this.http.put<string>(`${this.apiUrl}/${id}/deactivate`, null);
  }

  archiveSession(id: number): Observable<string> {
    return this.http.put<string>(`${this.apiUrl}/${id}/archive`, null);
  }

  deleteSession(id: number): Observable<string> {
    return this.http.delete<string>(`${this.apiUrl}/${id}`);
  }
}
