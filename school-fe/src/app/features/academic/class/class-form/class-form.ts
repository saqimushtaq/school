import { Component, inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { NgbActiveModal, NgbModalModule } from '@ng-bootstrap/ng-bootstrap';
import { ClassStore } from '../../../../stores/academic/class/class-store';
import { ClassResponse } from '../../../../stores/academic/class/class-types';
import { AcademicSessionStore } from '../../../../stores/academic/session/academic-session-store';

@Component({
  selector: 'app-class-form',
  imports: [NgbModalModule, ReactiveFormsModule],
  templateUrl: './class-form.html',
  styles: ``
})
export class ClassForm implements OnInit {
  modal = inject(NgbActiveModal);
  private store = inject(ClassStore);
  private sessionStore = inject(AcademicSessionStore);
  submitted = false;
  schoolClass: ClassResponse | null = null;

  isEdit = false;

  sessions = this.sessionStore.sessions;

  form: FormGroup = inject(FormBuilder).group({
    sessionId: ['', [Validators.required]],
    className: ['', [Validators.required]],
    section: ['', [Validators.maxLength(10)]],
    capacity: [null, [Validators.min(1)]],
  });

  ngOnInit(): void {
    // Load sessions for dropdown
    this.sessionStore.loadSessions({});

    if (this.schoolClass) {
      this.form.patchValue({
        sessionId: this.schoolClass.sessionId,
        className: this.schoolClass.className,
        section: this.schoolClass.section,
        capacity: this.schoolClass.capacity,
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

    if (this.isEdit && this.schoolClass?.id) {
      this.store.updateClass({ id: this.schoolClass.id, request: this.form.value });
      this.modal.close('success');
    } else {
      this.store.createClass({
        request: this.form.value,
        onSuccess: () => {
          this.form.reset();
          this.schoolClass = null;
          this.modal.close('success');
        }
      });
    }
  }

  get f() {
    return this.form.controls;
  }
}
