import { Component, computed, effect, inject, signal } from '@angular/core';
import { ClassStore } from '../../../../stores/academic/class/class-store';
import { DatePipe } from '@angular/common';
import { Breadcrumbs } from "../../../../shared/breadcrumbs/breadcrumbs";
import { FormsModule } from '@angular/forms';
import { NgbHighlight, NgbModal, NgbPaginationModule, NgbTooltipModule } from '@ng-bootstrap/ng-bootstrap';
import { Pagination } from "../../../../shared/pagination/pagination";
import { ClassResponse } from '../../../../stores/academic/class/class-types';
import { ClassForm } from '../class-form/class-form';
import { ConfirmModal } from '../../../../shared/confirm-modal/confirm-modal';
import { toObservable, toSignal } from '@angular/core/rxjs-interop';
import { debounceTime } from 'rxjs';
import { AcademicSessionStore } from '../../../../stores/academic/session/academic-session-store';

@Component({
  selector: 'app-class-list',
  imports: [
    Breadcrumbs,
    FormsModule,
    NgbHighlight,
    NgbPaginationModule,
    Pagination,
    NgbTooltipModule
  ],
  templateUrl: './class-list.html',
  styles: ``
})
export class ClassList {
  private modalService = inject(NgbModal);
  store = inject(ClassStore);
  sessionStore = inject(AcademicSessionStore);

  classes = this.store.classes;
  sessions = this.sessionStore.sessions;
  activeSession = this.sessionStore.activeSession;

  searchTerm = signal('');
  selectedSessionId = signal<number | null>(null);

  searchFor = toSignal(
    toObservable(this.searchTerm).pipe(debounceTime(500)),
    { initialValue: '' }
  );

  // Computed signal for filtered classes
  filteredClasses = computed(() => {
    const classes = this.classes();
    const search = this.searchFor().toLowerCase();

    if (!search) {
      return classes;
    }

    return classes.filter(c =>
      c.displayName.toLowerCase().includes(search) ||
      c.className.toLowerCase().includes(search) ||
      c.sessionName.toLowerCase().includes(search) ||
      (c.section && c.section.toLowerCase().includes(search))
    );
  });

  breadCrumbItems = [
    { label: 'Academic' },
    { label: 'Classes', active: true }
  ];

  constructor() {
    // Load sessions for filter dropdown
    this.sessionStore.loadSessions({});

    // Effect to load active session and its classes
    effect(() => {
      const active = this.activeSession();

      if (active && !this.selectedSessionId()) {
        // Set active session as default
        this.selectedSessionId.set(active.id);
      }
    }, { allowSignalWrites: true });

    // Effect to load classes based on selected session
    effect(() => {
      const sessionId = this.selectedSessionId();

      if (sessionId) {
        this.store.loadClassesBySession({ sessionId });
      }
    }, { allowSignalWrites: true });
  }

  openModal(schoolClass?: ClassResponse) {
    const ref = this.modalService.open(ClassForm, { centered: true, size: 'lg' });
    if (schoolClass) {
      ref.componentInstance.schoolClass = schoolClass;
    }
  }

  onDelete(schoolClass: ClassResponse) {
    const ref = this.modalService.open(ConfirmModal, { centered: true });

    ref.componentInstance.message = `Are you sure you want to delete ${schoolClass.displayName}?`;

    ref.result.then(() => {
      this.store.deleteClass(schoolClass.id);
    }).catch(() => {
      // User cancelled
    });
  }

  activate(schoolClass: ClassResponse) {
    const ref = this.modalService.open(ConfirmModal, { centered: true });

    ref.componentInstance.icon = 'bx bx-lg bx-question-mark text-info';
    ref.componentInstance.confirmButtonText = 'Yes, Activate it';
    ref.componentInstance.confirmButtonClass = 'btn w-sm btn-primary';
    ref.componentInstance.message = `Do you want to activate ${schoolClass.displayName}?`;

    ref.result.then(() => {
      this.store.activateClass(schoolClass.id);
    }).catch(() => {
      // User cancelled
    });
  }

  deactivate(schoolClass: ClassResponse) {
    const ref = this.modalService.open(ConfirmModal, { centered: true });

    ref.componentInstance.icon = 'bx bx-lg bx-question-mark text-info';
    ref.componentInstance.confirmButtonText = 'Yes, Deactivate it';
    ref.componentInstance.confirmButtonClass = 'btn w-sm btn-danger';
    ref.componentInstance.message = `Do you want to deactivate ${schoolClass.displayName}?`;

    ref.result.then(() => {
      this.store.deactivateClass(schoolClass.id);
    }).catch(() => {
      // User cancelled
    });
  }

  onPageChange(page: number) {
    const sessionId = this.selectedSessionId();
    if (sessionId) {
      this.store.loadClassesBySession({
        sessionId: sessionId,
        page: page - 1,
        size: this.store.pagination().size
      });
    }
  }
}
