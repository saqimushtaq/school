import { Routes } from '@angular/router';
import { Layout } from './layouts/layout/layout';
import { guestGuard } from './core/guards/guest-guard';
import { passwordChangeGuard } from './core/guards/password-change-guard';
import { authGuard } from './core/guards/auth-guard';

export const routes: Routes = [
  // Guest routes (accessible only when not logged in)
  {
    path: 'auth',
    canActivate: [guestGuard],
    children: [
      {
        path: 'login',
        loadComponent: () => import('./auth/login/login').then(m => m.Login)
      },
    ]
  },

  // Password change route (accessible only when logged in)
  // {
  //   path: 'auth/change-password',
  //   canActivate: [passwordChangeGuard],
  //   loadComponent: () => import('./pages/auth/change-password/change-password.component').then(m => m.ChangePasswordComponent)
  // },

  // Protected routes (require authentication)
  {
    path: '',
    component: Layout,
    canActivate: [authGuard],
    children: [
      {
        path: '',
        redirectTo: '/dashboard',
        pathMatch: 'full'
      },
      {
        path: 'dashboard',
        loadComponent: () => import('./features/dashboard/dashboard').then(m => m.Dashboard)
      },
    ]
  },

  // Admin routes (require ADMIN role)
  // {
  //   path: 'admin',
  //   canActivate: [roleGuard(['ADMIN'])],
  //   children: [
  //     {
  //       path: 'users',
  //       loadComponent: () => import('./pages/admin/users/users.component').then(m => m.UsersComponent)
  //     },
  //     {
  //       path: 'settings',
  //       loadComponent: () => import('./pages/admin/settings/settings.component').then(m => m.SettingsComponent)
  //     }
  //   ]
  // },

  // // Manager routes (require ADMIN or MANAGER role)
  // {
  //   path: 'management',
  //   canActivate: [roleGuard(['ADMIN', 'MANAGER'])],
  //   loadComponent: () => import('./pages/management/management.component').then(m => m.ManagementComponent)
  // },

  // // Using route data for permissions
  // {
  //   path: 'reports',
  //   canActivate: [permissionGuard],
  //   data: { requiredRoles: ['ADMIN', 'ANALYST'] },
  //   loadComponent: () => import('./pages/reports/reports.component').then(m => m.ReportsComponent)
  // },

  // // Unauthorized page
  // {
  //   path: 'unauthorized',
  //   loadComponent: () => import('./pages/unauthorized/unauthorized.component').then(m => m.UnauthorizedComponent)
  // },

  // Fallback route
  {
    path: '**',
    redirectTo: 'dashboard'
  }
];
