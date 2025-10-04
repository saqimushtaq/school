// grade-boundary-store.ts

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
import { GradeBoundaryService } from './grade-boundary-service';
import {
  GradeBoundaryState,
  initialGradeBoundaryState,
  GradeBoundaryRequest,
  GradeBoundary,
} from './grade-boundary-types';

export const GradeBoundaryStore = signalStore(
  { providedIn: 'root' },

  withState<GradeBoundaryState>(initialGradeBoundaryState),

  withComputed((store) => ({
    hasGradeBoundaries: computed(() => store.gradeBoundaries().length > 0),
    passingGrades: computed(() =>
      store.gradeBoundaries().filter(gb => gb.isPassing)
    ),
    failingGrades: computed(() =>
      store.gradeBoundaries().filter(gb => !gb.isPassing)
    ),
    sortedGradeBoundaries: computed(() =>
      [...store.gradeBoundaries()].sort((a, b) => b.minPercentage - a.minPercentage)
    ),
  })),

  withMethods((store, service = inject(GradeBoundaryService)) => ({

    // Load all grade boundaries
    loadGradeBoundaries: rxMethod<void>(
      pipe(
        tap(() => patchState(store, { isLoading: true, error: null })),
        switchMap(() =>
          service.getAllGradeBoundaries().pipe(
            tapResponse({
              next: (response) => {
                patchState(store, {
                  gradeBoundaries: response,
                  isLoading: false,
                });
              },
              error: (error: any) => {
                patchState(store, {
                  isLoading: false,
                  error: error?.error?.message || 'Failed to load grade boundaries',
                });
              },
            })
          )
        )
      )
    ),

    // Load grade boundary by ID
    loadGradeBoundaryById: rxMethod<number>(
      pipe(
        tap(() => patchState(store, { isLoading: true, error: null })),
        switchMap((id) =>
          service.getGradeBoundaryById(id).pipe(
            tapResponse({
              next: (response) => {
                patchState(store, {
                  selectedGradeBoundary: response,
                  isLoading: false,
                });
              },
              error: (error: any) => {
                patchState(store, {
                  isLoading: false,
                  error: error?.error?.message || 'Failed to load grade boundary',
                });
              },
            })
          )
        )
      )
    ),

    // Calculate grade for percentage
    calculateGrade: rxMethod<number>(
      pipe(
        tap(() => patchState(store, { isLoading: true, error: null })),
        switchMap((percentage) =>
          service.calculateGrade(percentage).pipe(
            tapResponse({
              next: () => {
                patchState(store, { isLoading: false });
              },
              error: (error: any) => {
                patchState(store, {
                  isLoading: false,
                  error: error?.error?.message || 'Failed to calculate grade',
                });
              },
            })
          )
        )
      )
    ),

    // Create grade boundary
    createGradeBoundary: rxMethod<{
      request: GradeBoundaryRequest;
      onSuccess?: () => void;
    }>(
      pipe(
        tap(() => patchState(store, { isLoading: true, error: null })),
        switchMap(({ request, onSuccess }) =>
          service.createGradeBoundary(request).pipe(
            tapResponse({
              next: (response) => {
                const currentBoundaries = store.gradeBoundaries();
                patchState(store, {
                  gradeBoundaries: [...currentBoundaries, response],
                  isLoading: false,
                });
                onSuccess?.();
              },
              error: (error: any) => {
                patchState(store, {
                  isLoading: false,
                  error: error?.error?.message || 'Failed to create grade boundary',
                });
              },
            })
          )
        )
      )
    ),

    // Update grade boundary
    updateGradeBoundary: rxMethod<{
      id: number;
      request: GradeBoundaryRequest;
      onSuccess?: () => void;
    }>(
      pipe(
        tap(() => patchState(store, { isLoading: true, error: null })),
        switchMap(({ id, request, onSuccess }) =>
          service.updateGradeBoundary(id, request).pipe(
            tapResponse({
              next: (response) => {
                const updatedBoundaries = store.gradeBoundaries().map((boundary) =>
                  boundary.id === id ? response : boundary
                );
                patchState(store, {
                  gradeBoundaries: updatedBoundaries,
                  selectedGradeBoundary:
                    store.selectedGradeBoundary()?.id === id
                      ? response
                      : store.selectedGradeBoundary(),
                  isLoading: false,
                });
                onSuccess?.();
              },
              error: (error: any) => {
                patchState(store, {
                  isLoading: false,
                  error: error?.error?.message || 'Failed to update grade boundary',
                });
              },
            })
          )
        )
      )
    ),

    // Delete grade boundary
    deleteGradeBoundary: rxMethod<number>(
      pipe(
        tap(() => patchState(store, { isLoading: true, error: null })),
        switchMap((id) =>
          service.deleteGradeBoundary(id).pipe(
            tapResponse({
              next: () => {
                const filteredBoundaries = store.gradeBoundaries().filter(
                  (boundary) => boundary.id !== id
                );
                patchState(store, {
                  gradeBoundaries: filteredBoundaries,
                  selectedGradeBoundary:
                    store.selectedGradeBoundary()?.id === id
                      ? null
                      : store.selectedGradeBoundary(),
                  isLoading: false,
                });
              },
              error: (error: any) => {
                patchState(store, {
                  isLoading: false,
                  error: error?.error?.message || 'Failed to delete grade boundary',
                });
              },
            })
          )
        )
      )
    ),

    // Setup default grade boundaries
    setupDefaultGradeBoundaries: rxMethod<{ onSuccess?: () => void }>(
      pipe(
        tap(() => patchState(store, { isLoading: true, error: null })),
        switchMap(({ onSuccess }) =>
          service.setupDefaultGradeBoundaries().pipe(
            tapResponse({
              next: (response) => {
                patchState(store, {
                  gradeBoundaries: response,
                  isLoading: false,
                });
                onSuccess?.();
              },
              error: (error: any) => {
                patchState(store, {
                  isLoading: false,
                  error: error?.error?.message || 'Failed to setup default grade boundaries',
                });
              },
            })
          )
        )
      )
    ),

    // Synchronous methods
    setSelectedGradeBoundary(boundary: GradeBoundary | null) {
      patchState(store, { selectedGradeBoundary: boundary });
    },

    clearError() {
      patchState(store, { error: null });
    },

    reset() {
      patchState(store, initialGradeBoundaryState);
    },
  })),

  withHooks({
    onInit(store) {
      // Load grade boundaries on initialization
      store.loadGradeBoundaries();
    },
  })
);
