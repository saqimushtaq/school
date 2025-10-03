import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivateFn, Router, RouterStateSnapshot } from '@angular/router';
import { AuthStore } from '../../stores/auth/auth-store';

export const roleGuard = (allowedRoles: string[]): CanActivateFn => {
  return (route: ActivatedRouteSnapshot, state: RouterStateSnapshot) => {
    const authStore = inject(AuthStore);
    const router = inject(Router);

    const isLoggedIn = authStore.isLoggedIn();

    if (!isLoggedIn) {
      return router.createUrlTree(['/auth/login'], {
        queryParams: { returnUrl: state.url }
      });
    }

    const roles = authStore.user()?.roles;
    const userHasRequiredRole = allowedRoles.some(role => hasRole(role, roles));

    if (userHasRequiredRole) {
      return true;
    }

    // Redirect to unauthorized page
    return router.createUrlTree(['/unauthorized']);
  };
};


function hasRole(role: string, roles?: string[]){
  if(!roles) return false;
  return roles.includes(role)
}
