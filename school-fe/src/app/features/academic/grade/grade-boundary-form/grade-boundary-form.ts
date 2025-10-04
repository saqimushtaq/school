import { Component, inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { NgbActiveModal, NgbModalModule } from '@ng-bootstrap/ng-bootstrap';
import { GradeBoundaryStore } from '../../../../stores/academic/grade/grade-boundary-store';
import { GradeBoundary } from '../../../../stores/academic/grade/grade-boundary-types';

@Component({
  selector: 'app-grade-boundary-form',
  imports: [NgbModalModule, ReactiveFormsModule],
  templateUrl: './grade-boundary-form.html',
  styles: ``
})
export class GradeBoundaryForm implements OnInit {
  modal = inject(NgbActiveModal);
  private store = inject(GradeBoundaryStore);
  submitted = false;
  gradeBoundary: GradeBoundary | null = null;

  isEdit = false;

  form: FormGroup = inject(FormBuilder).group({
    grade: ['', [Validators.required]],
    minPercentage: [0, [Validators.required, Validators.min(0), Validators.max(100)]],
    maxPercentage: [0, [Validators.required, Validators.min(0), Validators.max(100)]],
    isPassing: [true],
  });

  ngOnInit(): void {
    if (this.gradeBoundary) {
      this.form.patchValue(this.gradeBoundary);
      this.isEdit = true;
    }
  }

  save() {
    this.submitted = true;
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    if (this.isEdit && this.gradeBoundary?.id) {
      this.store.updateGradeBoundary({
        id: this.gradeBoundary.id,
        request: this.form.value,
        onSuccess: () => {
          this.modal.close('success');
        }
      });
    } else {
      this.store.createGradeBoundary({
        request: this.form.value,
        onSuccess: () => {
          this.form.reset({ isPassing: true });
          this.gradeBoundary = null;
          this.modal.close('success');
        }
      });
    }
  }

  get f() {
    return this.form.controls;
  }
}
