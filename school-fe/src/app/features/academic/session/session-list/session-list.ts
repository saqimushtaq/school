import { Component, effect, inject, Injector, OnInit, signal } from '@angular/core';
import { AcademicSessionStore } from '../../../../stores/academic/session/academic-session-store';
import { DatePipe, JsonPipe } from '@angular/common';
import { Breadcrumbs } from "../../../../shared/breadcrumbs/breadcrumbs";
import { FormsModule } from '@angular/forms';
import { NgbHighlight, NgbModal, NgbPaginationModule } from '@ng-bootstrap/ng-bootstrap';
import { Pagination } from "../../../../shared/pagination/pagination";
import { AcademicSessionResponse } from '../../../../stores/academic/session/academic-session-types';
import { SessionForm } from '../session-form/session-form';
import { ConfirmModal } from '../../../../shared/confirm-modal/confirm-modal';
import { toObservable, toSignal } from '@angular/core/rxjs-interop';
import { debounce, debounceTime, distinctUntilChanged } from 'rxjs';

@Component({
  selector: 'app-session-list',
  imports: [Breadcrumbs, FormsModule, NgbHighlight, DatePipe, NgbPaginationModule, Pagination],
  templateUrl: './session-list.html',
  styles: ``
})
export class SessionList implements OnInit {
  private modalServicee = inject(NgbModal)
  private injector = inject(Injector)
  store = inject(AcademicSessionStore)
  sessions = this.store.sessions
  searchTerm = signal('')
  searchFor = toSignal(toObservable(this.searchTerm).pipe(debounceTime(500)), {
    initialValue: '',
  });
  breadCrumbItems = [
    { label: 'Academic' },
    { label: 'Sessions', active: true }
  ]


  constructor() {
    effect(() => {
      this.store.loadSessions({ search: this.searchFor() })
    })
  }

  ngOnInit(): void {
    this.store.loadSessions({})
  }


  openModal(session?: AcademicSessionResponse) {
    if (session) {
      const ref = this.modalServicee.open(SessionForm, { centered: true })
      ref.componentInstance.session = session;
    } else {
      this.modalServicee.open(SessionForm, { centered: true })
    }
  }

  onDelete(session: AcademicSessionResponse) {
    const ref = this.modalServicee.open(ConfirmModal, { centered: true })

    ref.result.then(res => {
      this.store.deleteSession(session.id)
    })
  }

  activate(session: AcademicSessionResponse) {
    const ref = this.modalServicee.open(ConfirmModal, { centered: true })

    ref.componentInstance.icon = 'bx bx-lg bx-question-mark text-info';
    ref.componentInstance.confirmButtonText = 'Yes, Activate it';
    ref.componentInstance.confirmButtonClass = 'btn w-sm btn-primary';
    ref.componentInstance.message = 'Do you want to activate session ' + session.sessionName;

    ref.result.then(res => {
      this.store.activateSession(session.id)
    })
  }

  deactivate(session: AcademicSessionResponse) {
    const ref = this.modalServicee.open(ConfirmModal, { centered: true })

    ref.componentInstance.icon = 'bx bx-lg bx-question-mark text-info';
    ref.componentInstance.confirmButtonText = 'Yes, Deactivate it';
    ref.componentInstance.confirmButtonClass = 'btn w-sm btn-danger';
    ref.componentInstance.message = 'Do you want to de-activate session ' + session.sessionName;

    ref.result.then(res => {
      this.store.deactivateSession(session.id)
    })
  }

  onPageChange(page: number) {
    this.store.loadSessions({
      page: page - 1,  // ngb-pagination uses 1-based, backend uses 0-based
      size: this.store.pagination().size
    });
  }


}
