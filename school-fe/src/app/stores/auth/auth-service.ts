// auth.service.ts

import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { LoginRequest, LoginResponse, ChangePasswordRequest } from './auth-types';
import { ApiResponse } from '../common-types';


@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = 'http://localhost:8080/api/auth';

  login(request: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.apiUrl}/login`, request);
  }

  refreshToken(refreshToken: string): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(
      `${this.apiUrl}/refresh`,
      null,
      { headers: { Authorization: `Bearer ${refreshToken}` } }
    );
  }

  logout(token: string): Observable<string> {
    return this.http.post<string>(
      `${this.apiUrl}/logout`,
      null,
      { headers: { Authorization: `Bearer ${token}` } }
    );
  }

  changePassword(request: ChangePasswordRequest): Observable<string> {
    return this.http.post<string>(`${this.apiUrl}/change-password`, request);
  }

  resetPassword(userId: number, newPassword: string): Observable<string> {
    return this.http.post<string>(
      `${this.apiUrl}/reset-password/${userId}`,
      null,
      { params: { newPassword } }
    );
  }

  validateToken(token: string): Observable<boolean> {
    return this.http.get<boolean>(
      `${this.apiUrl}/validate`,
      { headers: { Authorization: `Bearer ${token}` } }
    );
  }
}
