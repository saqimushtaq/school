// class-subject-service.ts

import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ClassSubjectRequest, ClassSubjectResponse } from './class-subject-types';

@Injectable({
  providedIn: 'root'
})
export class ClassSubjectService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = 'http://localhost:8080/api/academic/class-subjects';

  assignSubjectToClass(request: ClassSubjectRequest): Observable<ClassSubjectResponse> {
    return this.http.post<ClassSubjectResponse>(this.apiUrl, request);
  }

  getClassSubjectById(id: number): Observable<ClassSubjectResponse> {
    return this.http.get<ClassSubjectResponse>(`${this.apiUrl}/${id}`);
  }

  getSubjectsByClass(classId: number): Observable<ClassSubjectResponse[]> {
    return this.http.get<ClassSubjectResponse[]>(`${this.apiUrl}/class/${classId}`);
  }

  getClassesBySubject(subjectId: number): Observable<ClassSubjectResponse[]> {
    return this.http.get<ClassSubjectResponse[]>(`${this.apiUrl}/subject/${subjectId}`);
  }

  updateClassSubject(id: number, request: ClassSubjectRequest): Observable<ClassSubjectResponse> {
    return this.http.put<ClassSubjectResponse>(`${this.apiUrl}/${id}`, request);
  }

  removeSubjectFromClass(id: number): Observable<string> {
    return this.http.delete<string>(`${this.apiUrl}/${id}`);
  }

  bulkAssignSubjectsToClass(classId: number, subjectIds: number[]): Observable<ClassSubjectResponse[]> {
    return this.http.post<ClassSubjectResponse[]>(
      `${this.apiUrl}/class/${classId}/bulk-assign`,
      subjectIds
    );
  }

  copySubjectsFromClass(sourceClassId: number, targetClassId: number): Observable<ClassSubjectResponse[]> {
    return this.http.post<ClassSubjectResponse[]>(
      `${this.apiUrl}/copy-subjects`,
      null,
      { params: { sourceClassId: sourceClassId.toString(), targetClassId: targetClassId.toString() } }
    );
  }
}
