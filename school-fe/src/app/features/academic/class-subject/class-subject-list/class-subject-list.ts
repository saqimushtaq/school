import { Component, computed, effect, inject, signal } from '@angular/core';
import { ClassSubjectStore } from '../../../../stores/academic/class-subject/class-subject-store';
import { Breadcrumbs } from "../../../../shared/breadcrumbs/breadcrumbs";
import { FormsModule } from '@angular/forms';
import { NgbHighlight, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { ClassSubjectResponse } from '../../../../stores/academic/class-subject/class-subject-types';
import { ClassSubjectForm } from '../class-subject-form/class-subject-form';
import { ClassSubjectBulkAssign } from '../class-subject-bulk-assign/class-subject-bulk-assign';
import { ConfirmModal } from '../../../../shared/confirm-modal/confirm-modal';
import { toObservable, toSignal } from '@angular/core/rxjs-interop';
import { debounceTime } from 'rxjs';
import { ClassSubjectCopyModal } from '../class-subject-copy-modal/class-subject-copy-modal';
import { ClassStore } from '../../../../stores/academic/class/class-store';

@Component({
  selector: 'app-class-subject-list',
  imports: [Breadcrumbs, FormsModule, NgbHighlight],
  templateUrl: './class-subject-list.html',
  styles: ``
})
export class ClassSubjectList {
  private modalService = inject(NgbModal);
  store = inject(ClassSubjectStore);
  private classStore = inject(ClassStore)
  classSubjects = this.store.classSubjects;


  searchTerm = signal('');
  selectedClassFilter = signal('');

  searchFor = toSignal(toObservable(this.searchTerm).pipe(debounceTime(500)), {
    initialValue: '',
  });

  classes = this.classStore.classes;

  breadCrumbItems = [
    { label: 'Academic' },
    { label: 'Class-Subject Assignments', active: true }
  ];

  // Computed filtered list
  filteredClassSubjects = computed(() => {
    let filtered = this.classSubjects();
    const search = this.searchFor().toLowerCase();
    const classFilter = this.selectedClassFilter();

    // Filter by class
    if (classFilter) {
      filtered = filtered.filter(cs => cs.classId === Number(classFilter));
    }

    // Filter by search term
    if (search) {
      filtered = filtered.filter(cs =>
        cs.className.toLowerCase().includes(search) ||
        cs.section.toLowerCase().includes(search) ||
        cs.subjectName.toLowerCase().includes(search) ||
        cs.subjectCode.toLowerCase().includes(search)
      );
    }

    return filtered;
  });

  constructor() {
    effect(() => {
      const search = this.searchFor();
      // Load data when search changes
    });
  }

  ngOnInit(): void {
    this.classStore.loadClasses({})
  }

  onFilterChange() {
    const classId = this.selectedClassFilter();
    if (classId) {
      this.store.loadSubjectsByClass(Number(classId));
    } else {
      // Load all - you might want to implement a loadAll method
      // For now, we'll just keep current data
    }
  }

  openModal(classSubject?: ClassSubjectResponse) {
    const ref = this.modalService.open(ClassSubjectForm, { centered: true, size: 'lg' });
    if (classSubject) {
      ref.componentInstance.classSubject = classSubject;
    }
  }

  openBulkAssignModal() {
    this.modalService.open(ClassSubjectBulkAssign, { centered: true, size: 'lg' });
  }

  openCopyModal() {
    this.modalService.open(ClassSubjectCopyModal, { centered: true });
  }

  onDelete(classSubject: ClassSubjectResponse) {
    const ref = this.modalService.open(ConfirmModal, { centered: true });

    ref.componentInstance.message = `Do you want to remove ${classSubject.subjectName} from ${classSubject.className} - ${classSubject.section}?`;

    ref.result.then(() => {
      this.store.removeSubjectFromClass(classSubject.id);
    }).catch(() => {
      // User cancelled
    });
  }
}
