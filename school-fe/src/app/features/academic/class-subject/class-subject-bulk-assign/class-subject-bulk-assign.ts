import { Component, inject, OnInit, signal } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { NgbActiveModal, NgbModalModule } from '@ng-bootstrap/ng-bootstrap';
import { ClassSubjectStore } from '../../../../stores/academic/class-subject/class-subject-store';
import { SubjectStore } from '../../../../stores/academic/subject/subject-store';
import { ClassStore } from '../../../../stores/academic/class/class-store';

@Component({
  selector: 'app-class-subject-bulk-assign',
  imports: [NgbModalModule, ReactiveFormsModule],
  templateUrl: './class-subject-bulk-assign.html',
  styles: ``
})
export class ClassSubjectBulkAssign implements OnInit {
  modal = inject(NgbActiveModal);
  private store = inject(ClassSubjectStore);
  private classStore = inject(ClassStore)
  private subjectStore = inject(SubjectStore);

  submitted = false;
  selectedSubjects = signal<number[]>([]);

  classes = this.classStore.classes;

  subjects = this.subjectStore.activeSubjects;

  form: FormGroup = inject(FormBuilder).group({
    classId: ['', [Validators.required]],
  });

  ngOnInit(): void {
    // Load active subjects for selection
    this.subjectStore.loadActiveSubjects();
    this.classStore.loadClasses({})
  }

  onSubjectToggle(subjectId: number, event: Event) {
    const checked = (event.target as HTMLInputElement).checked;
    const current = this.selectedSubjects();

    if (checked) {
      this.selectedSubjects.set([...current, subjectId]);
    } else {
      this.selectedSubjects.set(current.filter(id => id !== subjectId));
    }
  }

  save() {
    this.submitted = true;

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    if (this.selectedSubjects().length === 0) {
      return;
    }

    const classId = this.form.value.classId;

    this.store.bulkAssignSubjectsToClass({
      classId: classId,
      subjectIds: this.selectedSubjects(),
      onSuccess: () => {
        this.form.reset();
        this.selectedSubjects.set([]);
        this.modal.close('success');
      }
    });
  }

  get f() {
    return this.form.controls;
  }
}
