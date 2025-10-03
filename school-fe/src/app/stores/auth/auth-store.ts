// auth.store.ts

import { signalStore, withState, withMethods, withComputed, withHooks, withProps } from '@ngrx/signals';
import { computed, inject, effect } from '@angular/core';
import { patchState } from '@ngrx/signals';
import { rxMethod } from '@ngrx/signals/rxjs-interop';
import { pipe, switchMap, tap } from 'rxjs';
import { tapResponse } from '@ngrx/operators';
import { Router } from '@angular/router';
import { AuthService } from './auth-service';
import { AuthState, initialAuthState, LoginRequest, LoginResponse, ChangePasswordRequest, UserInfo } from './auth-types';

const AUTH_STORAGE_KEY = 'auth_state';

export const AuthStore = signalStore(
  { providedIn: 'root' },
  withState<AuthState>(initialAuthState),
  withProps(() => ({
    router: inject(Router)
  })),
  withComputed((store) => ({
    isLoggedIn: computed(() => store.isAuthenticated() && !!store.accessToken()),
    fullName: computed(() => {
      const user = store.user();
      return user ? `${user.firstName} ${user.lastName}` : '';
    }),
    needsPasswordChange: computed(() => store.user()?.mustChangePassword ?? false),
  })),
  withMethods((store, authService = inject(AuthService)) => ({
    // Login method using rxMethod
    login: rxMethod<LoginRequest>(
      pipe(
        tap(() => patchState(store, { isLoading: true, error: null })),
        switchMap((credentials) =>
          authService.login(credentials).pipe(
            tapResponse({
              next: (response) => {
                patchState(store, {
                    user: response.user,
                    accessToken: response.accessToken,
                    refreshToken: response.refreshToken,
                    isAuthenticated: true,
                    isLoading: false,
                    error: null,
                  });

                  // Navigate based on password change requirement
                  if (response.user.mustChangePassword) {
                    store.router.navigate(['/auth/change-password']);
                  } else {
                    store.router.navigate(['/']);
                  }
              },
              error: (error: any) => {
                const errorMessage = error?.error?.message || 'Login failed';
                patchState(store, {
                  user: null,
                  accessToken: null,
                  refreshToken: null,
                  isAuthenticated: false,
                  isLoading: false,
                  error: errorMessage,
                });
              },
            })
          )
        )
      )
    ),

    hasRole: (role: string) => {
      const roles = store.user()?.roles;
      return roles ? roles.includes(role) : false;
    },

    // Logout method using rxMethod
    logout: rxMethod<void>(
      pipe(
        tap(() => patchState(store, { isLoading: true })),
        switchMap(() => {
          const token = store.accessToken();
          if (!token) {
            patchState(store, initialAuthState);
            store.router.navigate(['/auth/login']);
            return [];
          }

          return authService.logout(token).pipe(
            tapResponse({
              next: () => {
                patchState(store, initialAuthState);
                clearAuthStorage();
                store.router.navigate(['/auth/login']);
              },
              error: () => {
                // Even if logout fails on server, clear local state
                patchState(store, initialAuthState);
                clearAuthStorage();
                store.router.navigate(['/auth/login']);
              },
            })
          );
        })
      )
    ),

    // Refresh token method using rxMethod
    refreshCurrentToken: rxMethod<void>(
      pipe(
        switchMap(() => {
          const refreshToken = store.refreshToken();
          if (!refreshToken) {
            patchState(store, initialAuthState);
            store.router.navigate(['/auth/login']);
            return [];
          }

          return authService.refreshToken(refreshToken).pipe(
            tapResponse({
              next: (response) => {
                patchState(store, {
                    accessToken: response.accessToken,
                    refreshToken: response.refreshToken,
                    user: response.user,
                  });
              },
              error: () => {
                patchState(store, initialAuthState);
                clearAuthStorage();
                store.router.navigate(['/auth/login']);
              },
            })
          );
        })
      )
    ),

    // Change password method using rxMethod
    changePassword: rxMethod<ChangePasswordRequest>(
      pipe(
        tap(() => patchState(store, { isLoading: true, error: null })),
        switchMap((request) =>
          authService.changePassword(request).pipe(
            tapResponse({
              next: (response) => {
                 const currentUser = store.user();
                  if (currentUser) {
                    patchState(store, {
                      user: { ...currentUser, mustChangePassword: false },
                      isLoading: false,
                    });
                  }
                  store.router.navigate(['/']);
              },
              error: (error: any) => {
                const errorMessage = error?.error?.message || 'Password change failed';
                patchState(store, {
                  isLoading: false,
                  error: errorMessage,
                });
              },
            })
          )
        )
      )
    ),

    // Simple synchronous methods
    setError(error: string | null) {
      patchState(store, { error });
    },

    clearError() {
      patchState(store, { error: null });
    },

    updateUser(user: Partial<UserInfo>) {
      const currentUser = store.user();
      if (currentUser) {
        patchState(store, {
          user: { ...currentUser, ...user },
        });
      }
    },
  })),
  withHooks({
    onInit(store) {
      // Load state from localStorage on initialization
      const storedState = loadAuthFromStorage();
      if (storedState) {
        patchState(store, storedState);
      }

      // Auto-save to localStorage whenever auth state changes
      effect(() => {
        const currentState = {
          user: store.user(),
          accessToken: store.accessToken(),
          refreshToken: store.refreshToken(),
          isAuthenticated: store.isAuthenticated(),
        };

        // Only save if user is authenticated
        if (store.isAuthenticated()) {
          saveAuthToStorage(currentState);
        }
      });
    },

    onDestroy() {
      console.log('AuthStore destroyed');
    },
  })
);

// Helper functions for localStorage
function loadAuthFromStorage(): Partial<AuthState> | null {
  try {
    const stored = localStorage.getItem(AUTH_STORAGE_KEY);
    if (!stored) return null;

    const parsed = JSON.parse(stored);

    // Validate that we have required fields
    if (parsed.accessToken && parsed.user) {
      return {
        user: parsed.user,
        accessToken: parsed.accessToken,
        refreshToken: parsed.refreshToken,
        isAuthenticated: parsed.isAuthenticated,
        isLoading: false,
        error: null,
      };
    }
    return null;
  } catch (error) {
    console.error('Error loading auth state from localStorage:', error);
    return null;
  }
}

function saveAuthToStorage(state: Partial<AuthState>): void {
  try {
    localStorage.setItem(AUTH_STORAGE_KEY, JSON.stringify(state));
  } catch (error) {
    console.error('Error saving auth state to localStorage:', error);
  }
}

function clearAuthStorage(): void {
  try {
    localStorage.removeItem(AUTH_STORAGE_KEY);
  } catch (error) {
    console.error('Error clearing auth state from localStorage:', error);
  }
}
