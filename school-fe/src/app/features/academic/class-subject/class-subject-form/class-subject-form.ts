import { Component, inject, OnInit, signal } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { NgbActiveModal, NgbModalModule } from '@ng-bootstrap/ng-bootstrap';
import { ClassSubjectStore } from '../../../../stores/academic/class-subject/class-subject-store';
import { ClassSubjectResponse } from '../../../../stores/academic/class-subject/class-subject-types';
import { SubjectStore } from '../../../../stores/academic/subject/subject-store';
import { ClassStore } from '../../../../stores/academic/class/class-store';

@Component({
  selector: 'app-class-subject-form',
  imports: [NgbModalModule, ReactiveFormsModule],
  templateUrl: './class-subject-form.html',
  styles: ``
})
export class ClassSubjectForm implements OnInit {
  modal = inject(NgbActiveModal);
  private store = inject(ClassSubjectStore);
  private subjectStore = inject(SubjectStore);
  private classStore = inject(ClassStore)

  submitted = false;
  classSubject: ClassSubjectResponse | null = null;
  isEdit = false;

  classes = this.classStore.classes;

  subjects = this.subjectStore.activeSubjects;

  form: FormGroup = inject(FormBuilder).group({
    classId: ['', [Validators.required]],
    subjectId: ['', [Validators.required]],
    totalMarks: ['', [Validators.required, Validators.min(1), Validators.max(1000)]],
    passingMarks: ['', [Validators.required, Validators.min(0)]],
  });

  ngOnInit(): void {
    // Load active subjects for dropdown
    this.subjectStore.loadActiveSubjects();
    this.classStore.loadClasses({})

    if (this.classSubject) {
      this.form.patchValue({
        classId: this.classSubject.classId,
        subjectId: this.classSubject.subjectId,
        totalMarks: this.classSubject.totalMarks,
        passingMarks: this.classSubject.passingMarks,
      });
      this.isEdit = true;
    }
  }

  save() {
    this.submitted = true;
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    if (this.isEdit && this.classSubject?.id) {
      this.store.updateClassSubject({
        id: this.classSubject.id,
        request: this.form.value
      });
      this.modal.close('success');
    } else {
      this.store.assignSubjectToClass({
        request: this.form.value,
        onSuccess: () => {
          this.form.reset();
          this.classSubject = null;
          this.modal.close('success');
        }
      });
    }
  }

  get f() {
    return this.form.controls;
  }
}
