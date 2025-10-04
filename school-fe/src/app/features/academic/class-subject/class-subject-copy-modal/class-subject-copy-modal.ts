import { Component, inject, signal } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { NgbActiveModal, NgbModalModule } from '@ng-bootstrap/ng-bootstrap';
import { ClassSubjectStore } from '../../../../stores/academic/class-subject/class-subject-store';
import { ClassStore } from '../../../../stores/academic/class/class-store';

@Component({
  selector: 'app-class-subject-copy-modal',
  imports: [NgbModalModule, ReactiveFormsModule],
  templateUrl: './class-subject-copy-modal.html',
  styles: ``
})
export class ClassSubjectCopyModal {
  modal = inject(NgbActiveModal);
  private store = inject(ClassSubjectStore);
  private classStore = inject(ClassStore);

  submitted = false;

  classes = this.classStore.classes;

  form: FormGroup = inject(FormBuilder).group({
    sourceClassId: ['', [Validators.required]],
    targetClassId: ['', [Validators.required]],
  });

  ngOnInit(): void {
    this.classStore.loadClasses({})
  }

  copy() {
    this.submitted = true;

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const { sourceClassId, targetClassId } = this.form.value;

    if (sourceClassId === targetClassId) {
      alert('Source and target class cannot be the same');
      return;
    }

    this.store.copySubjectsFromClass({
      sourceClassId: Number(sourceClassId),
      targetClassId: Number(targetClassId),
      onSuccess: () => {
        this.form.reset();
        this.modal.close('success');
      }
    });
  }

  get f() {
    return this.form.controls;
  }
}
