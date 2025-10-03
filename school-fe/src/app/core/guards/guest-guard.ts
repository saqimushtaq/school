// guest.guard.ts

import { inject } from '@angular/core';
import { CanActivateFn, Router, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { AuthStore } from '../../stores/auth/auth-store';


export const guestGuard: CanActivateFn = (
  route: ActivatedRouteSnapshot,
  state: RouterStateSnapshot
) => {
  const authStore = inject(AuthStore);
  const router = inject(Router);

  const isLoggedIn = authStore.isLoggedIn();

  if (!isLoggedIn) {
    // User is not logged in, allow access to guest routes
    return true;
  }

  // User is already logged in, redirect to home or dashboard
  // Check if there's a redirect parameter in query params
  const returnUrl = route.queryParams['returnUrl'] || '/';
  return router.createUrlTree([returnUrl]);
};
