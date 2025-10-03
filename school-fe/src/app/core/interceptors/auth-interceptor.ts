// core/interceptors/auth.interceptor.ts

import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthStore } from '../../stores/auth/auth-store';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authStore = inject(AuthStore);
  const token = authStore.accessToken();

  // Skip token attachment for auth endpoints
  const isAuthEndpoint = req.url.includes('/api/auth/login') ||
                         req.url.includes('/api/auth/refresh');

  if (token && !isAuthEndpoint) {
    // Clone request and add authorization header
    const clonedReq = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
    return next(clonedReq);
  }

  return next(req);
};
