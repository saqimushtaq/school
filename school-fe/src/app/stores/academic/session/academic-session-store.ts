// academic-session.store.ts

import {
  signalStore,
  withState,
  withMethods,
  withComputed,
  withHooks
} from '@ngrx/signals';
import { computed, inject } from '@angular/core';
import { patchState } from '@ngrx/signals';
import { rxMethod } from '@ngrx/signals/rxjs-interop';
import { pipe, switchMap, tap } from 'rxjs';
import { tapResponse } from '@ngrx/operators';
import { AcademicSessionService } from './academic-session-service';
import {
  AcademicSessionState,
  initialAcademicSessionState,
  AcademicSessionRequest,
  AcademicSessionResponse,
  SessionStatus,
} from './academic-session-types';
import { toPagination } from '../../common-types';

export const AcademicSessionStore = signalStore(
  { providedIn: 'root' },

  withState<AcademicSessionState>(initialAcademicSessionState),

  withComputed((store) => ({
    hasSessions: computed(() => store.sessions().length > 0),
    hasActiveSession: computed(() => store.activeSession() !== null),
    hasUpcomingSession: computed(() => store.upcomingSession() !== null),
    canLoadMore: computed(() => store.pagination().hasNext),
    currentPage: computed(() => store.pagination().page),
    totalPages: computed(() => store.pagination().totalPages),
  })),

  withMethods((store, sessionService = inject(AcademicSessionService)) => ({

    // Load all sessions
    loadSessions: rxMethod<{
      page?: number;
      size?: number;
      sortBy?: string;
      sortDir?: string;
      search?: string;
    }>(
      pipe(
        tap(() => patchState(store, { isLoading: true, error: null })),
        switchMap(({ page = 0, size = 10, sortBy = 'sessionName', sortDir = 'asc', search = '' }) =>
          sessionService.getAllSessions(page, size, sortBy, sortDir, search).pipe(
            tapResponse({
              next: (response) => {
                patchState(store, {
                    sessions: response.content,
                    pagination: toPagination(response),
                    isLoading: false,
                  });
              },
              error: (error: any) => {
                patchState(store, {
                  isLoading: false,
                  error: error?.error?.message || 'Failed to load sessions',
                });
              },
            })
          )
        )
      )
    ),

    // Load sessions by status
    loadSessionsByStatus: rxMethod<{
      status: SessionStatus;
      page?: number;
      size?: number;
    }>(
      pipe(
        tap(() => patchState(store, { isLoading: true, error: null })),
        switchMap(({ status, page = 0, size = 10 }) =>
          sessionService.getSessionsByStatus(status, page, size).pipe(
            tapResponse({
              next: (response) => {
               patchState(store, {
                    sessions: response.content,
                    pagination: toPagination(response),
                    isLoading: false,
                  });
              },
              error: (error: any) => {
                patchState(store, {
                  isLoading: false,
                  error: error?.error?.message || 'Failed to load sessions by status',
                });
              },
            })
          )
        )
      )
    ),

    // Load active session
    loadActiveSession: rxMethod<void>(
      pipe(
        tap(() => patchState(store, { isLoading: true, error: null })),
        switchMap(() =>
          sessionService.getActiveSession().pipe(
            tapResponse({
              next: (response) => {
                patchState(store, {
                    activeSession: response,
                    isLoading: false,
                  });
              },
              error: (error: any) => {
                patchState(store, {
                  isLoading: false,
                  error: error?.error?.message || 'Failed to load active session',
                });
              },
            })
          )
        )
      )
    ),

    // Load upcoming session
    loadUpcomingSession: rxMethod<void>(
      pipe(
        tap(() => patchState(store, { isLoading: true, error: null })),
        switchMap(() =>
          sessionService.getUpcomingSession().pipe(
            tapResponse({
              next: (response) => {
                patchState(store, {
                    upcomingSession: response,
                    isLoading: false,
                  });
              },
              error: (error: any) => {
                patchState(store, {
                  isLoading: false,
                  error: error?.error?.message || 'Failed to load upcoming session',
                });
              },
            })
          )
        )
      )
    ),

    // Load session by ID
    loadSessionById: rxMethod<number>(
      pipe(
        tap(() => patchState(store, { isLoading: true, error: null })),
        switchMap((id) =>
          sessionService.getSessionById(id).pipe(
            tapResponse({
              next: (response) => {
               patchState(store, {
                    selectedSession: response,
                    isLoading: false,
                  });
              },
              error: (error: any) => {
                patchState(store, {
                  isLoading: false,
                  error: error?.error?.message || 'Failed to load session',
                });
              },
            })
          )
        )
      )
    ),

    // Create session
    createSession: rxMethod<{request: AcademicSessionRequest, onSuccess?: () => void}>(
      pipe(
        tap(() => patchState(store, { isLoading: true, error: null })),
        switchMap(({request, onSuccess}) =>
          sessionService.createSession(request).pipe(
            tapResponse({
              next: (response) => {
                 const currentSessions = store.sessions();
                  patchState(store, {
                    sessions: [response, ...currentSessions],
                    isLoading: false,
                  });
                  onSuccess?.()
              },
              error: (error: any) => {
                patchState(store, {
                  isLoading: false,
                  error: error?.error?.message || 'Failed to create session',
                });
              },
            })
          )
        )
      )
    ),

    // Update session
    updateSession: rxMethod<{ id: number; request: AcademicSessionRequest }>(
      pipe(
        tap(() => patchState(store, { isLoading: true, error: null })),
        switchMap(({ id, request }) =>
          sessionService.updateSession(id, request).pipe(
            tapResponse({
              next: (response) => {
                const updatedSessions = store.sessions().map((session) =>
                    session.id === id ? response! : session
                  );
                  patchState(store, {
                    sessions: updatedSessions,
                    selectedSession:
                      store.selectedSession()?.id === id
                        ? response
                        : store.selectedSession(),
                    isLoading: false,
                  });
              },
              error: (error: any) => {
                patchState(store, {
                  isLoading: false,
                  error: error?.error?.message || 'Failed to update session',
                });
              },
            })
          )
        )
      )
    ),

    // Activate session
    activateSession: rxMethod<number>(
      pipe(
        tap(() => patchState(store, { isLoading: true, error: null })),
        switchMap((id) =>
          sessionService.activateSession(id).pipe(
            tapResponse({
              next: (response) => {
               const updatedSessions = store.sessions().map((session) =>
                    session.id === id
                      ? { ...session, status: SessionStatus.ACTIVE }
                      : session
                  );
                  patchState(store, {
                    sessions: updatedSessions,
                    isLoading: false,
                  });
              },
              error: (error: any) => {
                patchState(store, {
                  isLoading: false,
                  error: error?.error?.message || 'Failed to activate session',
                });
              },
            })
          )
        )
      )
    ),

    // Deactivate session
    deactivateSession: rxMethod<number>(
      pipe(
        tap(() => patchState(store, { isLoading: true, error: null })),
        switchMap((id) =>
          sessionService.deactivateSession(id).pipe(
            tapResponse({
              next: (response) => {
                const updatedSessions = store.sessions().map((session) =>
                    session.id === id
                      ? { ...session, status: SessionStatus.INACTIVE }
                      : session
                  );
                  patchState(store, {
                    sessions: updatedSessions,
                    isLoading: false,
                  });
              },
              error: (error: any) => {
                patchState(store, {
                  isLoading: false,
                  error: error?.error?.message || 'Failed to deactivate session',
                });
              },
            })
          )
        )
      )
    ),

    // Archive session
    archiveSession: rxMethod<number>(
      pipe(
        tap(() => patchState(store, { isLoading: true, error: null })),
        switchMap((id) =>
          sessionService.archiveSession(id).pipe(
            tapResponse({
              next: (response) => {
                 const updatedSessions = store.sessions().map((session) =>
                    session.id === id
                      ? { ...session, status: SessionStatus.ARCHIVED }
                      : session
                  );
                  patchState(store, {
                    sessions: updatedSessions,
                    isLoading: false,
                  });
              },
              error: (error: any) => {
                patchState(store, {
                  isLoading: false,
                  error: error?.error?.message || 'Failed to archive session',
                });
              },
            })
          )
        )
      )
    ),

    // Delete session
    deleteSession: rxMethod<number>(
      pipe(
        tap(() => patchState(store, { isLoading: true, error: null })),
        switchMap((id) =>
          sessionService.deleteSession(id).pipe(
            tapResponse({
              next: (response) => {
                 const filteredSessions = store.sessions().filter(
                    (session) => session.id !== id
                  );
                  patchState(store, {
                    sessions: filteredSessions,
                    selectedSession:
                      store.selectedSession()?.id === id
                        ? null
                        : store.selectedSession(),
                    isLoading: false,
                  });
              },
              error: (error: any) => {
                patchState(store, {
                  isLoading: false,
                  error: error?.error?.message || 'Failed to delete session',
                });
              },
            })
          )
        )
      )
    ),

    // Synchronous methods
    setSelectedSession(session: AcademicSessionResponse | null) {
      patchState(store, { selectedSession: session });
    },

    clearError() {
      patchState(store, { error: null });
    },

    reset() {
      patchState(store, initialAcademicSessionState);
    },
  })),

  withHooks({
    onInit(store) {
      // Load active session on initialization
      store.loadActiveSession();
    },
  })
);
