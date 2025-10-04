import { Component, effect, inject, signal } from '@angular/core';
import { SubjectStore } from '../../../../stores/academic/subject/subject-store';
import { DatePipe } from '@angular/common';
import { Breadcrumbs } from "../../../../shared/breadcrumbs/breadcrumbs";
import { FormsModule } from '@angular/forms';
import { NgbHighlight, NgbModal, NgbPaginationModule } from '@ng-bootstrap/ng-bootstrap';
import { Pagination } from "../../../../shared/pagination/pagination";
import { SubjectResponse } from '../../../../stores/academic/subject/subject-types';
import { SubjectForm } from '../subject-form/subject-form';
import { ConfirmModal } from '../../../../shared/confirm-modal/confirm-modal';
import { toObservable, toSignal } from '@angular/core/rxjs-interop';
import { debounceTime } from 'rxjs';

@Component({
  selector: 'app-subject-list',
  imports: [Breadcrumbs, FormsModule, NgbHighlight, NgbPaginationModule, Pagination],
  templateUrl: './subject-list.html',
  styles: ``
})
export class SubjectList {
  private modalService = inject(NgbModal);
  store = inject(SubjectStore);
  subjects = this.store.subjects;
  searchTerm = signal('');
  searchFor = toSignal(toObservable(this.searchTerm).pipe(debounceTime(500)), {
    initialValue: '',
  });

  breadCrumbItems = [
    { label: 'Academic' },
    { label: 'Subjects', active: true }
  ];

  constructor() {
    effect(() => {
      const search = this.searchFor();
      this.store.loadSubjects({});
    });
  }

  openModal(subject?: SubjectResponse) {
    const ref = this.modalService.open(SubjectForm, { centered: true });
    if (subject) {
      ref.componentInstance.subject = subject;
    }
  }

  onDelete(subject: SubjectResponse) {
    const ref = this.modalService.open(ConfirmModal, { centered: true });

    ref.componentInstance.message = 'Do you want to delete subject ' + subject.subjectName + '?';

    ref.result.then(() => {
      this.store.deleteSubject(subject.id);
    }).catch(() => {
      // User cancelled
    });
  }

  activate(subject: SubjectResponse) {
    const ref = this.modalService.open(ConfirmModal, { centered: true });

    ref.componentInstance.icon = 'bx bx-lg bx-question-mark text-info';
    ref.componentInstance.confirmButtonText = 'Yes, Activate it';
    ref.componentInstance.confirmButtonClass = 'btn w-sm btn-primary';
    ref.componentInstance.message = 'Do you want to activate subject ' + subject.subjectName + '?';

    ref.result.then(() => {
      this.store.activateSubject(subject.id);
    }).catch(() => {
      // User cancelled
    });
  }

  deactivate(subject: SubjectResponse) {
    const ref = this.modalService.open(ConfirmModal, { centered: true });

    ref.componentInstance.icon = 'bx bx-lg bx-question-mark text-info';
    ref.componentInstance.confirmButtonText = 'Yes, Deactivate it';
    ref.componentInstance.confirmButtonClass = 'btn w-sm btn-danger';
    ref.componentInstance.message = 'Do you want to deactivate subject ' + subject.subjectName + '?';

    ref.result.then(() => {
      this.store.deactivateSubject(subject.id);
    }).catch(() => {
      // User cancelled
    });
  }

  onPageChange(page: number) {
    this.store.loadSubjects({
      page: page - 1,  // ngb-pagination uses 1-based, backend uses 0-based
      size: this.store.pagination().size
    });
  }
}
