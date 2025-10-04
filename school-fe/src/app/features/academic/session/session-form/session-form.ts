import { Component, inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { NgbActiveModal, NgbModalModule } from '@ng-bootstrap/ng-bootstrap';
import { AcademicSessionStore } from '../../../../stores/academic/session/academic-session-store';
import { AcademicSessionResponse } from '../../../../stores/academic/session/academic-session-types';

@Component({
  selector: 'app-session-form',
  imports: [NgbModalModule, ReactiveFormsModule],
  templateUrl: './session-form.html',
  styles: ``
})
export class SessionForm implements OnInit {
  modal = inject(NgbActiveModal)
  private store = inject(AcademicSessionStore)
  submitted = false;
  session: AcademicSessionResponse | null = null

  isEdit = false

  form: FormGroup = inject(FormBuilder).group({
    sessionName: ['', [Validators.required]],
    startDate: ['', [Validators.required]],
    endDate: ['', [Validators.required]],

  })

  ngOnInit(): void {
    if (this.session) {
      this.form.patchValue(this.session)
      this.isEdit = true
    }
  }

  save() {
    this.submitted = true;
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return
    }

    if (this.isEdit && this.session?.id) {
      this.store.updateSession({ id: this.session.id, request: this.form.value })
    } else {
      this.store.createSession({
        request: this.form.value,
        onSuccess: () => {
          this.form.reset();
          this.session = null;
          this.modal.close('success')
        }
      })
    }

  }

  get f() {
    return this.form.controls
  }
}
