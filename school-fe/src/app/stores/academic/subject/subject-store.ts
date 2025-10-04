// subject-store.ts

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
import { SubjectService } from './subject-service';
import {
  SubjectState,
  initialSubjectState,
  SubjectRequest,
  SubjectResponse,
} from './subject-types';
import { toPagination } from '../../common-types';

export const SubjectStore = signalStore(
  { providedIn: 'root' },

  withState<SubjectState>(initialSubjectState),

  withComputed((store) => ({
    hasSubjects: computed(() => store.subjects().length > 0),
    hasActiveSubjects: computed(() => store.activeSubjects().length > 0),
    canLoadMore: computed(() => store.pagination().hasNext),
    currentPage: computed(() => store.pagination().page),
    totalPages: computed(() => store.pagination().totalPages),
  })),

  withMethods((store, subjectService = inject(SubjectService)) => ({

    // Load all subjects
    loadSubjects: rxMethod<{
      page?: number;
      size?: number;
      sortBy?: string;
      sortDir?: string;
    }>(
      pipe(
        tap(() => patchState(store, { isLoading: true, error: null })),
        switchMap(({ page = 0, size = 10, sortBy = 'subjectName', sortDir = 'asc' }) =>
          subjectService.getAllSubjects(page, size, sortBy, sortDir).pipe(
            tapResponse({
              next: (response) => {
                patchState(store, {
                  subjects: response.content,
                  pagination: toPagination(response),
                  isLoading: false,
                });
              },
              error: (error: any) => {
                patchState(store, {
                  isLoading: false,
                  error: error?.error?.message || 'Failed to load subjects',
                });
              },
            })
          )
        )
      )
    ),

    // Load active subjects
    loadActiveSubjects: rxMethod<void>(
      pipe(
        tap(() => patchState(store, { isLoading: true, error: null })),
        switchMap(() =>
          subjectService.getActiveSubjects().pipe(
            tapResponse({
              next: (response) => {
                patchState(store, {
                  activeSubjects: response,
                  isLoading: false,
                });
              },
              error: (error: any) => {
                patchState(store, {
                  isLoading: false,
                  error: error?.error?.message || 'Failed to load active subjects',
                });
              },
            })
          )
        )
      )
    ),

    // Load subjects by status
    loadSubjectsByStatus: rxMethod<{
      isActive: boolean;
      page?: number;
      size?: number;
    }>(
      pipe(
        tap(() => patchState(store, { isLoading: true, error: null })),
        switchMap(({ isActive, page = 0, size = 10 }) =>
          subjectService.getSubjectsByStatus(isActive, page, size).pipe(
            tapResponse({
              next: (response) => {
                patchState(store, {
                  subjects: response.content,
                  pagination: toPagination(response),
                  isLoading: false,
                });
              },
              error: (error: any) => {
                patchState(store, {
                  isLoading: false,
                  error: error?.error?.message || 'Failed to load subjects by status',
                });
              },
            })
          )
        )
      )
    ),

    // Load subject by ID
    loadSubjectById: rxMethod<number>(
      pipe(
        tap(() => patchState(store, { isLoading: true, error: null })),
        switchMap((id) =>
          subjectService.getSubjectById(id).pipe(
            tapResponse({
              next: (response) => {
                patchState(store, {
                  selectedSubject: response,
                  isLoading: false,
                });
              },
              error: (error: any) => {
                patchState(store, {
                  isLoading: false,
                  error: error?.error?.message || 'Failed to load subject',
                });
              },
            })
          )
        )
      )
    ),

    // Create subject
    createSubject: rxMethod<{ request: SubjectRequest; onSuccess?: () => void }>(
      pipe(
        tap(() => patchState(store, { isLoading: true, error: null })),
        switchMap(({ request, onSuccess }) =>
          subjectService.createSubject(request).pipe(
            tapResponse({
              next: (response) => {
                const currentSubjects = store.subjects();
                patchState(store, {
                  subjects: [response, ...currentSubjects],
                  isLoading: false,
                });
                onSuccess?.();
              },
              error: (error: any) => {
                patchState(store, {
                  isLoading: false,
                  error: error?.error?.message || 'Failed to create subject',
                });
              },
            })
          )
        )
      )
    ),

    // Update subject
    updateSubject: rxMethod<{ id: number; request: SubjectRequest }>(
      pipe(
        tap(() => patchState(store, { isLoading: true, error: null })),
        switchMap(({ id, request }) =>
          subjectService.updateSubject(id, request).pipe(
            tapResponse({
              next: (response) => {
                const updatedSubjects = store.subjects().map((subject) =>
                  subject.id === id ? response : subject
                );
                patchState(store, {
                  subjects: updatedSubjects,
                  selectedSubject:
                    store.selectedSubject()?.id === id
                      ? response
                      : store.selectedSubject(),
                  isLoading: false,
                });
              },
              error: (error: any) => {
                patchState(store, {
                  isLoading: false,
                  error: error?.error?.message || 'Failed to update subject',
                });
              },
            })
          )
        )
      )
    ),

    // Activate subject
    activateSubject: rxMethod<number>(
      pipe(
        tap(() => patchState(store, { isLoading: true, error: null })),
        switchMap((id) =>
          subjectService.activateSubject(id).pipe(
            tapResponse({
              next: () => {
                const updatedSubjects = store.subjects().map((subject) =>
                  subject.id === id ? { ...subject, isActive: true } : subject
                );
                patchState(store, {
                  subjects: updatedSubjects,
                  isLoading: false,
                });
              },
              error: (error: any) => {
                patchState(store, {
                  isLoading: false,
                  error: error?.error?.message || 'Failed to activate subject',
                });
              },
            })
          )
        )
      )
    ),

    // Deactivate subject
    deactivateSubject: rxMethod<number>(
      pipe(
        tap(() => patchState(store, { isLoading: true, error: null })),
        switchMap((id) =>
          subjectService.deactivateSubject(id).pipe(
            tapResponse({
              next: () => {
                const updatedSubjects = store.subjects().map((subject) =>
                  subject.id === id ? { ...subject, isActive: false } : subject
                );
                patchState(store, {
                  subjects: updatedSubjects,
                  isLoading: false,
                });
              },
              error: (error: any) => {
                patchState(store, {
                  isLoading: false,
                  error: error?.error?.message || 'Failed to deactivate subject',
                });
              },
            })
          )
        )
      )
    ),

    // Delete subject
    deleteSubject: rxMethod<number>(
      pipe(
        tap(() => patchState(store, { isLoading: true, error: null })),
        switchMap((id) =>
          subjectService.deleteSubject(id).pipe(
            tapResponse({
              next: () => {
                const filteredSubjects = store.subjects().filter(
                  (subject) => subject.id !== id
                );
                patchState(store, {
                  subjects: filteredSubjects,
                  selectedSubject:
                    store.selectedSubject()?.id === id
                      ? null
                      : store.selectedSubject(),
                  isLoading: false,
                });
              },
              error: (error: any) => {
                patchState(store, {
                  isLoading: false,
                  error: error?.error?.message || 'Failed to delete subject',
                });
              },
            })
          )
        )
      )
    ),

    // Synchronous methods
    setSelectedSubject(subject: SubjectResponse | null) {
      patchState(store, { selectedSubject: subject });
    },

    clearError() {
      patchState(store, { error: null });
    },

    reset() {
      patchState(store, initialSubjectState);
    },
  })),

  withHooks({
    onInit(store) {
      // Load active subjects on initialization
      store.loadActiveSubjects();
    },
  })
);
