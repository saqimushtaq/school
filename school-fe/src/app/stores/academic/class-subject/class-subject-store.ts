// class-subject-store.ts

import {
  signalStore,
  withState,
  withMethods,
  withComputed,
} from '@ngrx/signals';
import { computed, inject } from '@angular/core';
import { patchState } from '@ngrx/signals';
import { rxMethod } from '@ngrx/signals/rxjs-interop';
import { pipe, switchMap, tap } from 'rxjs';
import { tapResponse } from '@ngrx/operators';
import { ClassSubjectService } from './class-subject-service';
import {
  ClassSubjectState,
  initialClassSubjectState,
  ClassSubjectRequest,
  ClassSubjectResponse,
} from './class-subject-types';

export const ClassSubjectStore = signalStore(
  { providedIn: 'root' },

  withState<ClassSubjectState>(initialClassSubjectState),

  withComputed((store) => ({
    hasClassSubjects: computed(() => store.classSubjects().length > 0),
    hasSubjectsByClass: computed(() => store.subjectsByClass().size > 0),
    hasClassesBySubject: computed(() => store.classesBySubject().size > 0),
  })),

  withMethods((store, classSubjectService = inject(ClassSubjectService)) => ({

    // Load subjects by class
    loadSubjectsByClass: rxMethod<number>(
      pipe(
        tap(() => patchState(store, { isLoading: true, error: null })),
        switchMap((classId) =>
          classSubjectService.getSubjectsByClass(classId).pipe(
            tapResponse({
              next: (response) => {
                const subjectsByClass = new Map(store.subjectsByClass());
                subjectsByClass.set(classId, response);
                patchState(store, {
                  classSubjects: response,
                  subjectsByClass,
                  isLoading: false,
                });
              },
              error: (error: any) => {
                patchState(store, {
                  isLoading: false,
                  error: error?.error?.message || 'Failed to load subjects by class',
                });
              },
            })
          )
        )
      )
    ),

    // Load classes by subject
    loadClassesBySubject: rxMethod<number>(
      pipe(
        tap(() => patchState(store, { isLoading: true, error: null })),
        switchMap((subjectId) =>
          classSubjectService.getClassesBySubject(subjectId).pipe(
            tapResponse({
              next: (response) => {
                const classesBySubject = new Map(store.classesBySubject());
                classesBySubject.set(subjectId, response);
                patchState(store, {
                  classSubjects: response,
                  classesBySubject,
                  isLoading: false,
                });
              },
              error: (error: any) => {
                patchState(store, {
                  isLoading: false,
                  error: error?.error?.message || 'Failed to load classes by subject',
                });
              },
            })
          )
        )
      )
    ),

    // Load class-subject by ID
    loadClassSubjectById: rxMethod<number>(
      pipe(
        tap(() => patchState(store, { isLoading: true, error: null })),
        switchMap((id) =>
          classSubjectService.getClassSubjectById(id).pipe(
            tapResponse({
              next: (response) => {
                patchState(store, {
                  selectedClassSubject: response,
                  isLoading: false,
                });
              },
              error: (error: any) => {
                patchState(store, {
                  isLoading: false,
                  error: error?.error?.message || 'Failed to load class-subject',
                });
              },
            })
          )
        )
      )
    ),

    // Assign subject to class
    assignSubjectToClass: rxMethod<{ request: ClassSubjectRequest; onSuccess?: () => void }>(
      pipe(
        tap(() => patchState(store, { isLoading: true, error: null })),
        switchMap(({ request, onSuccess }) =>
          classSubjectService.assignSubjectToClass(request).pipe(
            tapResponse({
              next: (response) => {
                const currentClassSubjects = store.classSubjects();

                // Update the main list
                patchState(store, {
                  classSubjects: [response, ...currentClassSubjects],
                  isLoading: false,
                });

                // Update the subjectsByClass map
                const subjectsByClass = new Map(store.subjectsByClass());
                const classSubjects = subjectsByClass.get(request.classId) || [];
                subjectsByClass.set(request.classId, [response, ...classSubjects]);
                patchState(store, { subjectsByClass });

                onSuccess?.();
              },
              error: (error: any) => {
                patchState(store, {
                  isLoading: false,
                  error: error?.error?.message || 'Failed to assign subject to class',
                });
              },
            })
          )
        )
      )
    ),

    // Update class-subject
    updateClassSubject: rxMethod<{ id: number; request: ClassSubjectRequest }>(
      pipe(
        tap(() => patchState(store, { isLoading: true, error: null })),
        switchMap(({ id, request }) =>
          classSubjectService.updateClassSubject(id, request).pipe(
            tapResponse({
              next: (response) => {
                const updatedClassSubjects = store.classSubjects().map((cs) =>
                  cs.id === id ? response : cs
                );
                patchState(store, {
                  classSubjects: updatedClassSubjects,
                  selectedClassSubject:
                    store.selectedClassSubject()?.id === id
                      ? response
                      : store.selectedClassSubject(),
                  isLoading: false,
                });

                // Update the subjectsByClass map
                const subjectsByClass = new Map(store.subjectsByClass());
                const classSubjects = subjectsByClass.get(request.classId) || [];
                const updatedClassSubjects2 = classSubjects.map((cs) =>
                  cs.id === id ? response : cs
                );
                subjectsByClass.set(request.classId, updatedClassSubjects2);
                patchState(store, { subjectsByClass });
              },
              error: (error: any) => {
                patchState(store, {
                  isLoading: false,
                  error: error?.error?.message || 'Failed to update class-subject',
                });
              },
            })
          )
        )
      )
    ),

    // Remove subject from class
    removeSubjectFromClass: rxMethod<number>(
      pipe(
        tap(() => patchState(store, { isLoading: true, error: null })),
        switchMap((id) =>
          classSubjectService.removeSubjectFromClass(id).pipe(
            tapResponse({
              next: () => {
                const filteredClassSubjects = store.classSubjects().filter(
                  (cs) => cs.id !== id
                );
                patchState(store, {
                  classSubjects: filteredClassSubjects,
                  selectedClassSubject:
                    store.selectedClassSubject()?.id === id
                      ? null
                      : store.selectedClassSubject(),
                  isLoading: false,
                });

                // Update all maps by removing the deleted entry
                const subjectsByClass = new Map(store.subjectsByClass());
                subjectsByClass.forEach((subjects, classId) => {
                  const filtered = subjects.filter((cs) => cs.id !== id);
                  subjectsByClass.set(classId, filtered);
                });
                patchState(store, { subjectsByClass });
              },
              error: (error: any) => {
                patchState(store, {
                  isLoading: false,
                  error: error?.error?.message || 'Failed to remove subject from class',
                });
              },
            })
          )
        )
      )
    ),

    // Bulk assign subjects to class
    bulkAssignSubjectsToClass: rxMethod<{ classId: number; subjectIds: number[]; onSuccess?: () => void }>(
      pipe(
        tap(() => patchState(store, { isLoading: true, error: null })),
        switchMap(({ classId, subjectIds, onSuccess }) =>
          classSubjectService.bulkAssignSubjectsToClass(classId, subjectIds).pipe(
            tapResponse({
              next: (response) => {
                const currentClassSubjects = store.classSubjects();
                patchState(store, {
                  classSubjects: [...response, ...currentClassSubjects],
                  isLoading: false,
                });

                // Update the subjectsByClass map
                const subjectsByClass = new Map(store.subjectsByClass());
                const classSubjects = subjectsByClass.get(classId) || [];
                subjectsByClass.set(classId, [...response, ...classSubjects]);
                patchState(store, { subjectsByClass });

                onSuccess?.();
              },
              error: (error: any) => {
                patchState(store, {
                  isLoading: false,
                  error: error?.error?.message || 'Failed to bulk assign subjects',
                });
              },
            })
          )
        )
      )
    ),

    // Copy subjects from one class to another
    copySubjectsFromClass: rxMethod<{ sourceClassId: number; targetClassId: number; onSuccess?: () => void }>(
      pipe(
        tap(() => patchState(store, { isLoading: true, error: null })),
        switchMap(({ sourceClassId, targetClassId, onSuccess }) =>
          classSubjectService.copySubjectsFromClass(sourceClassId, targetClassId).pipe(
            tapResponse({
              next: (response) => {
                const currentClassSubjects = store.classSubjects();
                patchState(store, {
                  classSubjects: [...response, ...currentClassSubjects],
                  isLoading: false,
                });

                // Update the subjectsByClass map for target class
                const subjectsByClass = new Map(store.subjectsByClass());
                const targetClassSubjects = subjectsByClass.get(targetClassId) || [];
                subjectsByClass.set(targetClassId, [...response, ...targetClassSubjects]);
                patchState(store, { subjectsByClass });

                onSuccess?.();
              },
              error: (error: any) => {
                patchState(store, {
                  isLoading: false,
                  error: error?.error?.message || 'Failed to copy subjects',
                });
              },
            })
          )
        )
      )
    ),

    // Get subjects for a specific class from cache
    getSubjectsForClass(classId: number): ClassSubjectResponse[] {
      return store.subjectsByClass().get(classId) || [];
    },

    // Get classes for a specific subject from cache
    getClassesForSubject(subjectId: number): ClassSubjectResponse[] {
      return store.classesBySubject().get(subjectId) || [];
    },

    // Synchronous methods
    setSelectedClassSubject(classSubject: ClassSubjectResponse | null) {
      patchState(store, { selectedClassSubject: classSubject });
    },

    clearError() {
      patchState(store, { error: null });
    },

    reset() {
      patchState(store, initialClassSubjectState);
    },
  }))
);
