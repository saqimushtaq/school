import { Component, inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { NgbActiveModal, NgbModalModule } from '@ng-bootstrap/ng-bootstrap';
import { SubjectStore } from '../../../../stores/academic/subject/subject-store';
import { SubjectResponse } from '../../../../stores/academic/subject/subject-types';

@Component({
  selector: 'app-subject-form',
  imports: [NgbModalModule, ReactiveFormsModule],
  templateUrl: './subject-form.html',
  styles: ``
})
export class SubjectForm implements OnInit {
  modal = inject(NgbActiveModal);
  private store = inject(SubjectStore);
  submitted = false;
  subject: SubjectResponse | null = null;

  isEdit = false;

  form: FormGroup = inject(FormBuilder).group({
    subjectName: ['', [Validators.required]],
    subjectCode: ['', [Validators.required]],
    isActive: [true],
  });

  ngOnInit(): void {
    if (this.subject) {
      this.form.patchValue(this.subject);
      this.isEdit = true;
    }
  }

  save() {
    this.submitted = true;
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    if (this.isEdit && this.subject?.id) {
      this.store.updateSubject({
        id: this.subject.id,
        request: this.form.value
      });
      this.modal.close('success');
    } else {
      this.store.createSubject({
        request: this.form.value,
        onSuccess: () => {
          this.form.reset();
          this.subject = null;
          this.modal.close('success');
        }
      });
    }
  }

  get f() {
    return this.form.controls;
  }
}
