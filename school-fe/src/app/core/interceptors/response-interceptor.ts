// core/interceptors/response.interceptor.ts

import { HttpInterceptorFn, HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { tap, catchError } from 'rxjs/operators';
import { throwError } from 'rxjs';
import { ApiResponse } from '../../stores/common-types';

export const responseInterceptor: HttpInterceptorFn = (req, next) => {
  return next(req).pipe(
    tap((event) => {
      // Handle successful responses
      if (event instanceof HttpResponse) {
        const body = event.body;

        // Check if it's an ApiResponse structure
        if (body && typeof body === 'object' && 'success' in body) {
          const apiResponse = body as ApiResponse<any>;

          // Log success messages for POST, PUT, PATCH, DELETE
          if (['POST', 'PUT', 'PATCH', 'DELETE'].includes(req.method)) {
            if (apiResponse.message) {
              console.log('✅ Success:', apiResponse.message);
              // TODO: Replace with toastr notification
              // this.toastr.success(apiResponse.message);
            }
          }
        }
      }
    }),
    catchError((error: HttpErrorResponse) => {
      // Handle error responses
      let errorMessage = 'An unexpected error occurred';

      if (error.error instanceof ErrorEvent) {
        // Client-side error
        errorMessage = `Client Error: ${error.error.message}`;
        console.error('❌ Client Error:', error.error.message);
      } else {
        // Server-side error
        if (error.error && typeof error.error === 'object' && 'message' in error.error) {
          errorMessage = error.error.message;
        } else if (error.message) {
          errorMessage = error.message;
        }

        console.error('❌ Server Error:', {
          status: error.status,
          message: errorMessage,
          url: error.url
        });
      }

      // TODO: Replace with toastr notification
      // this.toastr.error(errorMessage);

      return throwError(() => error);
    })
  );
};
