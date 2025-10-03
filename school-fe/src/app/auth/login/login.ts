import { Component, inject } from '@angular/core';
import { AuthStore } from '../../stores/auth/auth-store';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { ToastService } from './toast-service';
import { ToastsContainer } from "./toasts-container.component";

@Component({
  selector: 'app-login',
  imports: [ToastsContainer, ReactiveFormsModule],
  templateUrl: './login.html',
  styles: ``
})
export class Login {

  private auth = inject(AuthStore)
  form: FormGroup = inject(FormBuilder).group({
    username: ['admin', [Validators.required]],
    password: ['Admin@123', [Validators.required]],
  })
  private router = inject(Router)
  private toastService = inject(ToastService)
  // Login Form
  submitted = false;
  fieldTextType!: boolean;
  error = '';
  returnUrl!: string;

  toast!: false;

  // set the current year
  year: number = new Date().getFullYear();

  constructor() {
    // redirect to home if already logged in
    if (this.auth.isAuthenticated()) {
      this.router.navigate(['/']);
    }
  }

  ngOnInit(): void {

  }

  // convenience getter for easy access to form fields
  get f() { return this.form.controls; }

  /**
   * Form submit
   */
  onSubmit() {
    this.submitted = true;
    if (this.form.invalid) {
      this.form.markAllAsTouched()
      return;
    }
    // Login Api
    this.auth.login(this.form.value);

  }

  /**
   * Password Hide/Show
   */
  toggleFieldTextType() {
    this.fieldTextType = !this.fieldTextType;
  }

}
