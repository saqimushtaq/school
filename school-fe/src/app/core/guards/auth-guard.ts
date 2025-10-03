import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivateFn, Router, RouterStateSnapshot } from '@angular/router';
import { AuthStore } from '../../stores/auth/auth-store';

export const authGuard: CanActivateFn = (
  route: ActivatedRouteSnapshot,
  state: RouterStateSnapshot
) => {
  const authStore = inject(AuthStore);
  const router = inject(Router);

  const isLoggedIn = authStore.isLoggedIn();

  if (isLoggedIn) {
    // Check if user needs to change password
    const needsPasswordChange = authStore.needsPasswordChange();
    const isPasswordChangePage = state.url.includes('/auth/change-password');

    // If user must change password and not already on change password page
    if (needsPasswordChange && !isPasswordChangePage) {
      return router.createUrlTree(['/auth/change-password']);
    }

    return true;
  }

  // Store the attempted URL for redirecting after login
  return router.createUrlTree(['/auth/login'], {
    queryParams: { returnUrl: state.url }
  });
};
