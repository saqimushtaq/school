// core/interceptors/unwrap-response.interceptor.ts

import { HttpInterceptorFn, HttpResponse } from '@angular/common/http';
import { map } from 'rxjs/operators';
import { ApiResponse } from '../../stores/common-types';

/**
 * Unwraps ApiResponse<T> and returns just the data T
 * Services will receive the unwrapped data directly
 */
export const unwrapResponseInterceptor: HttpInterceptorFn = (req, next) => {
  return next(req).pipe(
    map((event) => {
      if (event instanceof HttpResponse) {
        const body = event.body;

        // Check if it's an ApiResponse structure
        if (body && typeof body === 'object' && 'success' in body && 'data' in body) {
          const apiResponse = body as ApiResponse<any>;

          // Return a new HttpResponse with unwrapped data
          return event.clone({
            body: apiResponse.data
          });
        }
      }
      return event;
    })
  );
};
