import {Component, inject, signal} from '@angular/core';
import {AbstractControl, FormControl, FormGroup, ReactiveFormsModule, ValidationErrors, ValidatorFn, Validators} from '@angular/forms';
import {HttpErrorResponse} from '@angular/common/http';
import {AuthService} from '../../core/api-services/auth.service';
import {RouterNavigationService} from '../../core/services/router-navigation.service';
import {UserInfoService} from '../../core/services/user-info.service';
import {TranslatePipe, TranslateService} from '@ngx-translate/core';
import {MessageService} from 'primeng/api';
import {Button} from 'primeng/button';
import {CardModule} from 'primeng/card';
import {FloatLabelModule} from 'primeng/floatlabel';
import {PasswordModule} from 'primeng/password';
import {FaIconComponent} from '@fortawesome/angular-fontawesome';
import {faKey, faShieldHalved} from '@fortawesome/free-solid-svg-icons';

export const passwordChangeValidator: ValidatorFn = (control: AbstractControl): ValidationErrors | null => {
  const currentPassword = control.get('currentPassword')?.value;
  const newPassword = control.get('newPassword')?.value;
  const confirmPassword = control.get('confirmPassword')?.value;

  const errors: ValidationErrors = {};

  if (currentPassword && newPassword && currentPassword === newPassword) {
    errors['sameAsCurrent'] = true;
  }

  if (newPassword && confirmPassword && newPassword !== confirmPassword) {
    errors['passwordMismatch'] = true;
  }

  return Object.keys(errors).length > 0 ? errors : null;
};

const ERROR_CODE_TRANSLATION_KEYS: ReadonlyMap<string, string> = new Map([
  ['INVALID_CURRENT_PASSWORD', 'settings.security.error.invalid-current-password'],
  ['SAME_PASSWORD', 'settings.security.error.same-password'],
  ['NO_PASSWORD_CREDENTIALS', 'settings.security.error.no-credentials'],
  ['VALIDATION_FAILED', 'settings.security.error.validation']
]);

@Component({
  selector: 'app-settings-security',
  imports: [
    ReactiveFormsModule,
    TranslatePipe,
    Button,
    CardModule,
    FloatLabelModule,
    PasswordModule,
    FaIconComponent
  ],
  templateUrl: './settings-security.component.html',
  styleUrl: './settings-security.component.css'
})
export class SettingsSecurityComponent {
  private readonly authService = inject(AuthService);
  private readonly router = inject(RouterNavigationService);
  private readonly userInfoService = inject(UserInfoService);
  private readonly translate = inject(TranslateService);
  private readonly messageService = inject(MessageService, { optional: true });

  protected readonly faShieldHalved = faShieldHalved;
  protected readonly faKey = faKey;

  readonly loading = signal<boolean>(false);

  readonly passwordForm = new FormGroup(
    {
      currentPassword: new FormControl<string>('', {
        nonNullable: true,
        validators: [Validators.required]
      }),
      newPassword: new FormControl<string>('', {
        nonNullable: true,
        validators: [Validators.required, Validators.minLength(8)]
      }),
      confirmPassword: new FormControl<string>('', {
        nonNullable: true,
        validators: [Validators.required]
      })
    },
    { validators: passwordChangeValidator }
  );

  get currentPasswordControl(): FormControl<string> {
    return this.passwordForm.controls.currentPassword;
  }

  get newPasswordControl(): FormControl<string> {
    return this.passwordForm.controls.newPassword;
  }

  get confirmPasswordControl(): FormControl<string> {
    return this.passwordForm.controls.confirmPassword;
  }

  hasSamePasswordError(): boolean {
    return !!this.passwordForm.errors?.['sameAsCurrent'];
  }

  hasPasswordMismatch(): boolean {
    return !!this.passwordForm.errors?.['passwordMismatch'];
  }

  onSubmit(): void {
    if (this.loading()) {
      return;
    }

    if (this.passwordForm.invalid) {
      this.passwordForm.markAllAsTouched();
      return;
    }

    const { currentPassword, newPassword } = this.passwordForm.getRawValue();

    this.loading.set(true);

    this.authService.changePassword(currentPassword, newPassword).subscribe({
      next: () => {
        this.loading.set(false);
        const summary = this.translate.instant('settings.security.success.summary');
        const detail = this.translate.instant('settings.security.success.detail');
        this.notifySuccess(summary, detail);
        this.authService.logout(false);
        this.router.login();
      },
      error: (err: unknown) => {
        this.loading.set(false);
        const summary = this.translate.instant('settings.security.error.summary');
        const errorKey = (err instanceof HttpErrorResponse && err.status === 400)
          ? ERROR_CODE_TRANSLATION_KEYS.get(err.error?.code)
          : undefined;
        const detail = this.translate.instant(errorKey ?? 'settings.security.error.generic');

        this.notifyError(summary, detail);
      }
    });
  }

  private notifySuccess(summary: string, detail: string): void {
    if (this.messageService) {
      this.messageService.add({ severity: 'success', summary, detail, life: 5000 });
    } else {
      this.userInfoService.success(summary, detail);
    }
  }

  private notifyError(summary: string, detail: string): void {
    if (this.messageService) {
      this.messageService.add({ severity: 'error', summary, detail, life: 5000 });
    } else {
      this.userInfoService.error(summary, detail);
    }
  }
}
