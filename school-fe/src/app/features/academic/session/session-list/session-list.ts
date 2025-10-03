import { Component, inject, OnInit } from '@angular/core';
import { AcademicSessionStore } from '../../../../stores/academic/session/academic-session-store';
import { DatePipe, JsonPipe } from '@angular/common';
import { Breadcrumbs } from "../../../../shared/breadcrumbs/breadcrumbs";
import { FormsModule } from '@angular/forms';
import { NgbHighlight, NgbModal, NgbPaginationModule } from '@ng-bootstrap/ng-bootstrap';
import { Pagination } from "../../../../shared/pagination/pagination";
import { AcademicSessionResponse } from '../../../../stores/academic/session/academic-session-types';
import { SessionForm } from '../session-form/session-form';

@Component({
  selector: 'app-session-list',
  imports: [Breadcrumbs, FormsModule, NgbHighlight, DatePipe, NgbPaginationModule, Pagination],
  templateUrl: './session-list.html',
  styles: ``
})
export class SessionList implements OnInit {
  private modalServicee = inject(NgbModal)
  store = inject(AcademicSessionStore)
  sessions = this.store.sessions
  searchTerm = ''
  breadCrumbItems = [
    { label: 'Academic' },
    { label: 'Sessions', active: true }
  ]


  ngOnInit(): void {
    this.store.loadSessions({})
  }

  performSearch() {

  }

  openModal(session?: AcademicSessionResponse) {
    if (session) {
      const ref = this.modalServicee.open(SessionForm, { centered: true })
      ref.componentInstance.session = session;
    } else {
      this.modalServicee.open(SessionForm, { centered: true })
    }
  }

  onPageChange(page: number) {
    this.store.loadSessions({
      page: page - 1,  // ngb-pagination uses 1-based, backend uses 0-based
      size: this.store.pagination().size
    });
  }


}
