import { Component, computed, inject, signal } from '@angular/core';
import { GradeBoundaryStore } from '../../../../stores/academic/grade/grade-boundary-store';
import { DatePipe } from '@angular/common';
import { Breadcrumbs } from "../../../../shared/breadcrumbs/breadcrumbs";
import { FormsModule } from '@angular/forms';
import { NgbHighlight, NgbModal, NgbTooltip } from '@ng-bootstrap/ng-bootstrap';
import { GradeBoundary } from '../../../../stores/academic/grade/grade-boundary-types';
import { GradeBoundaryForm } from '../grade-boundary-form/grade-boundary-form';
import { ConfirmModal } from '../../../../shared/confirm-modal/confirm-modal';

@Component({
  selector: 'app-grade-boundary-list',
  imports: [
    Breadcrumbs,
    FormsModule,
    NgbHighlight,
    NgbTooltip
  ],
  templateUrl: './grade-boundary-list.html',
  styles: ``
})
export class GradeBoundaryList {
  private modalService = inject(NgbModal);
  store = inject(GradeBoundaryStore);

  searchTerm = signal('');

  filteredBoundaries = computed(() => {
    const term = this.searchTerm().toLowerCase();
    if (!term) {
      return this.store.sortedGradeBoundaries();
    }
    return this.store.sortedGradeBoundaries().filter(boundary =>
      boundary.grade.toLowerCase().includes(term) ||
      boundary.minPercentage.toString().includes(term) ||
      boundary.maxPercentage.toString().includes(term)
    );
  });

  breadCrumbItems = [
    { label: 'Academic' },
    { label: 'Grade Boundaries', active: true }
  ];

  openModal(boundary?: GradeBoundary) {
    const ref = this.modalService.open(GradeBoundaryForm, { centered: true });
    if (boundary) {
      ref.componentInstance.gradeBoundary = boundary;
    }
  }

  onDelete(boundary: GradeBoundary) {
    const ref = this.modalService.open(ConfirmModal, { centered: true });

    ref.componentInstance.message = `Do you want to delete grade boundary "${boundary.grade}"?`;

    ref.result.then(() => {
      this.store.deleteGradeBoundary(boundary.id);
    }).catch(() => {
      // User cancelled
    });
  }

  setupDefaults() {
    const ref = this.modalService.open(ConfirmModal, { centered: true });

    ref.componentInstance.icon = 'bx bx-lg bx-question-mark text-info';
    ref.componentInstance.confirmButtonText = 'Yes, Setup Defaults';
    ref.componentInstance.confirmButtonClass = 'btn w-sm btn-primary';
    ref.componentInstance.message = 'This will create default grade boundaries (A+, A, B+, B, C+, C, D, F). Any existing boundaries will be preserved.';

    ref.result.then(() => {
      this.store.setupDefaultGradeBoundaries({
        onSuccess: () => {
          // Success handled by store
        }
      });
    }).catch(() => {
      // User cancelled
    });
  }
}
