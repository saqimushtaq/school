import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivateFn, Router, RouterStateSnapshot } from '@angular/router';
import { AuthStore } from '../../stores/auth/auth-store';

export const passwordChangeGuard: CanActivateFn = (
  route: ActivatedRouteSnapshot,
  state: RouterStateSnapshot
) => {
  const authStore = inject(AuthStore);
  const router = inject(Router);

  const isLoggedIn = authStore.isLoggedIn();

  if (!isLoggedIn) {
    return router.createUrlTree(['/auth/login']);
  }

  // Allow access if user must change password
  return true;
};
