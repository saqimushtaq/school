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
  private readonly apiUrl = '/api/auth';

  login(request: LoginRequest): Observable<ApiResponse<LoginResponse>> {
    return this.http.post<ApiResponse<LoginResponse>>(`${this.apiUrl}/login`, request);
  }

  refreshToken(refreshToken: string): Observable<ApiResponse<LoginResponse>> {
    return this.http.post<ApiResponse<LoginResponse>>(
      `${this.apiUrl}/refresh`,
      null,
      { headers: { Authorization: `Bearer ${refreshToken}` } }
    );
  }

  logout(token: string): Observable<ApiResponse<string>> {
    return this.http.post<ApiResponse<string>>(
      `${this.apiUrl}/logout`,
      null,
      { headers: { Authorization: `Bearer ${token}` } }
    );
  }

  changePassword(request: ChangePasswordRequest): Observable<ApiResponse<string>> {
    return this.http.post<ApiResponse<string>>(`${this.apiUrl}/change-password`, request);
  }

  resetPassword(userId: number, newPassword: string): Observable<ApiResponse<string>> {
    return this.http.post<ApiResponse<string>>(
      `${this.apiUrl}/reset-password/${userId}`,
      null,
      { params: { newPassword } }
    );
  }

  validateToken(token: string): Observable<ApiResponse<boolean>> {
    return this.http.get<ApiResponse<boolean>>(
      `${this.apiUrl}/validate`,
      { headers: { Authorization: `Bearer ${token}` } }
    );
  }
}
