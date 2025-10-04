// grade-boundary-service.ts

import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { GradeBoundary, GradeBoundaryRequest } from './grade-boundary-types';
import { ApiResponse } from '../../common-types';

@Injectable({
  providedIn: 'root'
})
export class GradeBoundaryService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = 'http://localhost:8080/api/academic/grade-boundaries';

  createGradeBoundary(request: GradeBoundaryRequest): Observable<GradeBoundary> {
    const params = new HttpParams()
      .set('grade', request.grade)
      .set('minPercentage', request.minPercentage.toString())
      .set('maxPercentage', request.maxPercentage.toString())
      .set('isPassing', request.isPassing.toString());

    return this.http.post<GradeBoundary>(this.apiUrl, null, { params });
  }

  getAllGradeBoundaries(): Observable<GradeBoundary[]> {
    return this.http.get<GradeBoundary[]>(this.apiUrl);
  }

  getGradeBoundaryById(id: number): Observable<GradeBoundary> {
    return this.http.get<GradeBoundary>(`${this.apiUrl}/${id}`);
  }

  calculateGrade(percentage: number): Observable<string> {
    const params = new HttpParams().set('percentage', percentage.toString());
    return this.http.get<string>(`${this.apiUrl}/calculate-grade`, { params });
  }

  updateGradeBoundary(
    id: number,
    request: GradeBoundaryRequest
  ): Observable<GradeBoundary> {
    const params = new HttpParams()
      .set('grade', request.grade)
      .set('minPercentage', request.minPercentage.toString())
      .set('maxPercentage', request.maxPercentage.toString())
      .set('isPassing', request.isPassing.toString());

    return this.http.put<GradeBoundary>(`${this.apiUrl}/${id}`, null, { params });
  }

  deleteGradeBoundary(id: number): Observable<string> {
    return this.http.delete<string>(`${this.apiUrl}/${id}`);
  }

  setupDefaultGradeBoundaries(): Observable<GradeBoundary[]> {
    return this.http.post<GradeBoundary[]>(`${this.apiUrl}/setup-defaults`, null);
  }
}
