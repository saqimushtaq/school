import { inject } from "@angular/core";
import { CanActivateFn, ActivatedRouteSnapshot, RouterStateSnapshot, Router } from "@angular/router";
import { AuthStore } from "../../stores/auth/auth-store";

export const permissionGuard: CanActivateFn = (
  route: ActivatedRouteSnapshot,
  state: RouterStateSnapshot
) => {
  const authStore = inject(AuthStore);
  const router = inject(Router);

  const isLoggedIn = authStore.isLoggedIn();

  if (!isLoggedIn) {
    return router.createUrlTree(['/auth/login'], {
      queryParams: { returnUrl: state.url }
    });
  }

  // Get required roles from route data
  const requiredRoles = route.data['requiredRoles'] as string[] | undefined;

  if (!requiredRoles || requiredRoles.length === 0) {
    return true;
  }

  const roles = authStore.user()?.roles

  const userHasRequiredRole = requiredRoles.some(role => hasRole(role, roles));

  if (userHasRequiredRole) {
    return true;
  }

  return router.createUrlTree(['/unauthorized']);
};


function hasRole(role: string, roles?: string[]){
  if(!roles) return false;
  return roles.includes(role)
}
