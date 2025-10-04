// school-class.store.ts

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
import { ClassService } from './class-service';
import {
  ClassState,
  initialClassState,
  ClassRequest,
  ClassResponse,
} from './class-types';
import { toPagination } from '../../common-types';

export const ClassStore = signalStore(
  { providedIn: 'root' },

  withState<ClassState>(initialClassState),

  withComputed((store) => ({
    hasClasses: computed(() => store.classes().length > 0),
    hasActiveClasses: computed(() => store.activeClasses().length > 0),
    canLoadMore: computed(() => store.pagination().hasNext),
    currentPage: computed(() => store.pagination().page),
    totalPages: computed(() => store.pagination().totalPages),
  })),

  withMethods((store, classService = inject(ClassService)) => ({

    // Load all classes
    loadClasses: rxMethod<{
      page?: number;
      size?: number;
      sortBy?: string;
      sortDir?: string;
    }>(
      pipe(
        tap(() => patchState(store, { isLoading: true, error: null })),
        switchMap(({ page = 0, size = 10, sortBy = 'className', sortDir = 'asc' }) =>
          classService.getAllClasses(page, size, sortBy, sortDir).pipe(
            tapResponse({
              next: (response) => {
                patchState(store, {
                  classes: response.content,
                  pagination: toPagination(response),
                  isLoading: false,
                });
              },
              error: (error: any) => {
                patchState(store, {
                  isLoading: false,
                  error: error?.error?.message || 'Failed to load classes',
                });
              },
            })
          )
        )
      )
    ),

    // Load classes by session
    loadClassesBySession: rxMethod<{
      sessionId: number;
      page?: number;
      size?: number;
    }>(
      pipe(
        tap(() => patchState(store, { isLoading: true, error: null })),
        switchMap(({ sessionId, page = 0, size = 10 }) =>
          classService.getClassesBySession(sessionId, page, size).pipe(
            tapResponse({
              next: (response) => {
                patchState(store, {
                  classes: response.content,
                  pagination: toPagination(response),
                  isLoading: false,
                });
              },
              error: (error: any) => {
                patchState(store, {
                  isLoading: false,
                  error: error?.error?.message || 'Failed to load classes by session',
                });
              },
            })
          )
        )
      )
    ),

    // Load active classes by session
    loadActiveClassesBySession: rxMethod<number>(
      pipe(
        tap(() => patchState(store, { isLoading: true, error: null })),
        switchMap((sessionId) =>
          classService.getActiveClassesBySession(sessionId).pipe(
            tapResponse({
              next: (response) => {
                patchState(store, {
                  activeClasses: response,
                  isLoading: false,
                });
              },
              error: (error: any) => {
                patchState(store, {
                  isLoading: false,
                  error: error?.error?.message || 'Failed to load active classes',
                });
              },
            })
          )
        )
      )
    ),

    // Load class by ID
    loadClassById: rxMethod<number>(
      pipe(
        tap(() => patchState(store, { isLoading: true, error: null })),
        switchMap((id) =>
          classService.getClassById(id).pipe(
            tapResponse({
              next: (response) => {
                patchState(store, {
                  selectedClass: response,
                  isLoading: false,
                });
              },
              error: (error: any) => {
                patchState(store, {
                  isLoading: false,
                  error: error?.error?.message || 'Failed to load class',
                });
              },
            })
          )
        )
      )
    ),

    // Create class
    createClass: rxMethod<{ request: ClassRequest; onSuccess?: () => void }>(
      pipe(
        tap(() => patchState(store, { isLoading: true, error: null })),
        switchMap(({ request, onSuccess }) =>
          classService.createClass(request).pipe(
            tapResponse({
              next: (response) => {
                const currentClasses = store.classes();
                patchState(store, {
                  classes: [response, ...currentClasses],
                  isLoading: false,
                });
                onSuccess?.();
              },
              error: (error: any) => {
                patchState(store, {
                  isLoading: false,
                  error: error?.error?.message || 'Failed to create class',
                });
              },
            })
          )
        )
      )
    ),

    // Update class
    updateClass: rxMethod<{ id: number; request: ClassRequest }>(
      pipe(
        tap(() => patchState(store, { isLoading: true, error: null })),
        switchMap(({ id, request }) =>
          classService.updateClass(id, request).pipe(
            tapResponse({
              next: (response) => {
                const updatedClasses = store.classes().map((schoolClass) =>
                  schoolClass.id === id ? response : schoolClass
                );
                patchState(store, {
                  classes: updatedClasses,
                  selectedClass:
                    store.selectedClass()?.id === id
                      ? response
                      : store.selectedClass(),
                  isLoading: false,
                });
              },
              error: (error: any) => {
                patchState(store, {
                  isLoading: false,
                  error: error?.error?.message || 'Failed to update class',
                });
              },
            })
          )
        )
      )
    ),

    // Activate class
    activateClass: rxMethod<number>(
      pipe(
        tap(() => patchState(store, { isLoading: true, error: null })),
        switchMap((id) =>
          classService.activateClass(id).pipe(
            tapResponse({
              next: () => {
                const updatedClasses = store.classes().map((schoolClass) =>
                  schoolClass.id === id
                    ? { ...schoolClass, isActive: true }
                    : schoolClass
                );
                patchState(store, {
                  classes: updatedClasses,
                  isLoading: false,
                });
              },
              error: (error: any) => {
                patchState(store, {
                  isLoading: false,
                  error: error?.error?.message || 'Failed to activate class',
                });
              },
            })
          )
        )
      )
    ),

    // Deactivate class
    deactivateClass: rxMethod<number>(
      pipe(
        tap(() => patchState(store, { isLoading: true, error: null })),
        switchMap((id) =>
          classService.deactivateClass(id).pipe(
            tapResponse({
              next: () => {
                const updatedClasses = store.classes().map((schoolClass) =>
                  schoolClass.id === id
                    ? { ...schoolClass, isActive: false }
                    : schoolClass
                );
                patchState(store, {
                  classes: updatedClasses,
                  isLoading: false,
                });
              },
              error: (error: any) => {
                patchState(store, {
                  isLoading: false,
                  error: error?.error?.message || 'Failed to deactivate class',
                });
              },
            })
          )
        )
      )
    ),

    // Delete class
    deleteClass: rxMethod<number>(
      pipe(
        tap(() => patchState(store, { isLoading: true, error: null })),
        switchMap((id) =>
          classService.deleteClass(id).pipe(
            tapResponse({
              next: () => {
                const filteredClasses = store.classes().filter(
                  (schoolClass) => schoolClass.id !== id
                );
                patchState(store, {
                  classes: filteredClasses,
                  selectedClass:
                    store.selectedClass()?.id === id
                      ? null
                      : store.selectedClass(),
                  isLoading: false,
                });
              },
              error: (error: any) => {
                patchState(store, {
                  isLoading: false,
                  error: error?.error?.message || 'Failed to delete class',
                });
              },
            })
          )
        )
      )
    ),

    // Synchronous methods
    setSelectedClass(schoolClass: ClassResponse | null) {
      patchState(store, { selectedClass: schoolClass });
    },

    clearError() {
      patchState(store, { error: null });
    },

    reset() {
      patchState(store, initialClassState);
    },
  }))
);
