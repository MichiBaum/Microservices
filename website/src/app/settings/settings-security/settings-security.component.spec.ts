import { describe, it, expect, beforeEach, vi } from 'vitest';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpErrorResponse } from '@angular/common/http';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { provideTranslateService, TranslateService } from '@ngx-translate/core';
import { of, throwError } from 'rxjs';
import { MessageService } from 'primeng/api';
import { AuthService } from '../../core/api-services/auth.service';
import { RouterNavigationService } from '../../core/services/router-navigation.service';
import { UserInfoService } from '../../core/services/user-info.service';
import { SettingsSecurityComponent, passwordChangeValidator } from './settings-security.component';
import { FormControl, FormGroup } from '@angular/forms';

describe('SettingsSecurityComponent', () => {
  let component: SettingsSecurityComponent;
  let fixture: ComponentFixture<SettingsSecurityComponent>;

  let authServiceMock: {
    changePassword: ReturnType<typeof vi.fn>;
    logout: ReturnType<typeof vi.fn>;
  };
  let routerMock: {
    login: ReturnType<typeof vi.fn>;
  };
  let userInfoServiceMock: {
    success: ReturnType<typeof vi.fn>;
    error: ReturnType<typeof vi.fn>;
  };
  let messageServiceMock: {
    add: ReturnType<typeof vi.fn>;
  };

  const translations = {
    settings: {
      security: {
        'change-password': 'Change Password',
        'change-password-description': 'Update your account password to keep your account secure.',
        success: {
          summary: 'Password updated',
          detail: 'Your password has been changed successfully. Please log in again.'
        },
        error: {
          summary: 'Password change failed',
          generic: 'An unexpected error occurred while changing your password. Please try again.',
          'invalid-current-password': 'The current password you entered is incorrect.',
          'same-password': 'The new password must be different from your current password.',
          'no-credentials': 'Your account does not use password authentication.',
          validation: 'The entered passwords are not valid.'
        }
      }
    }
  };

  beforeEach(async () => {
    authServiceMock = {
      changePassword: vi.fn(),
      logout: vi.fn()
    };
    routerMock = {
      login: vi.fn()
    };
    userInfoServiceMock = {
      success: vi.fn(),
      error: vi.fn()
    };
    messageServiceMock = {
      add: vi.fn()
    };

    await TestBed.configureTestingModule({
      imports: [SettingsSecurityComponent],
      providers: [
        provideAnimationsAsync(),
        provideTranslateService({
          fallbackLang: 'en',
          lang: 'en'
        }),
        { provide: AuthService, useValue: authServiceMock },
        { provide: RouterNavigationService, useValue: routerMock },
        { provide: UserInfoService, useValue: userInfoServiceMock },
        { provide: MessageService, useValue: messageServiceMock }
      ]
    }).compileComponents();

    const translate = TestBed.inject(TranslateService);
    translate.setTranslation('en', translations);
    translate.use('en');

    fixture = TestBed.createComponent(SettingsSecurityComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  describe('Form Initialization and Validation', () => {
    it('should initialize with empty controls and invalid form state', () => {
      expect(component.passwordForm).toBeDefined();
      expect(component.currentPasswordControl.value).toBe('');
      expect(component.newPasswordControl.value).toBe('');
      expect(component.confirmPasswordControl.value).toBe('');
      expect(component.passwordForm.invalid).toBe(true);
      expect(component.loading()).toBe(false);
    });

    it('should require currentPassword, newPassword, and confirmPassword', () => {
      expect(component.currentPasswordControl.errors?.['required']).toBe(true);
      expect(component.newPasswordControl.errors?.['required']).toBe(true);
      expect(component.confirmPasswordControl.errors?.['required']).toBe(true);

      component.currentPasswordControl.setValue('oldPassword');
      expect(component.currentPasswordControl.errors).toBeNull();
    });

    it('should enforce minimum 8 characters on newPassword', () => {
      component.newPasswordControl.setValue('short7');
      expect(component.newPasswordControl.errors?.['minlength']).toBeDefined();
      expect(component.newPasswordControl.errors?.['minlength'].requiredLength).toBe(8);

      component.newPasswordControl.setValue('validLength8');
      expect(component.newPasswordControl.errors).toBeNull();
    });

    it('should flag sameAsCurrent error when newPassword equals currentPassword', () => {
      component.passwordForm.setValue({
        currentPassword: 'SamePassword123!',
        newPassword: 'SamePassword123!',
        confirmPassword: 'SamePassword123!'
      });

      expect(component.passwordForm.errors?.['sameAsCurrent']).toBe(true);
      expect(component.hasSamePasswordError()).toBe(true);
      expect(component.hasPasswordMismatch()).toBe(false);
      expect(component.passwordForm.invalid).toBe(true);
    });

    it('should flag passwordMismatch error when confirmPassword does not match newPassword', () => {
      component.passwordForm.setValue({
        currentPassword: 'OldPassword123!',
        newPassword: 'NewSecurePassword456!',
        confirmPassword: 'MismatchPassword789!'
      });

      expect(component.passwordForm.errors?.['passwordMismatch']).toBe(true);
      expect(component.hasPasswordMismatch()).toBe(true);
      expect(component.hasSamePasswordError()).toBe(false);
      expect(component.passwordForm.invalid).toBe(true);
    });

    it('should flag both sameAsCurrent and passwordMismatch when applicable', () => {
      component.passwordForm.setValue({
        currentPassword: 'Password123!',
        newPassword: 'Password123!',
        confirmPassword: 'DifferentPassword456!'
      });

      expect(component.passwordForm.errors?.['sameAsCurrent']).toBe(true);
      expect(component.passwordForm.errors?.['passwordMismatch']).toBe(true);
      expect(component.hasSamePasswordError()).toBe(true);
      expect(component.hasPasswordMismatch()).toBe(true);
      expect(component.passwordForm.invalid).toBe(true);
    });

    it('should be valid when all requirements and cross-field rules are met', () => {
      component.passwordForm.setValue({
        currentPassword: 'OldPassword123!',
        newPassword: 'NewSecurePassword456!',
        confirmPassword: 'NewSecurePassword456!'
      });

      expect(component.passwordForm.errors).toBeNull();
      expect(component.hasSamePasswordError()).toBe(false);
      expect(component.hasPasswordMismatch()).toBe(false);
      expect(component.passwordForm.valid).toBe(true);
    });

    it('passwordChangeValidator standalone function handles missing controls gracefully', () => {
      const partialGroup = new FormGroup({
        currentPassword: new FormControl(''),
        newPassword: new FormControl('')
      });
      expect(passwordChangeValidator(partialGroup)).toBeNull();
    });
  });

  describe('Form Submission Guards', () => {
    it('should not call changePassword and should mark all controls touched when form is invalid', () => {
      expect(component.currentPasswordControl.touched).toBe(false);
      expect(component.newPasswordControl.touched).toBe(false);
      expect(component.confirmPasswordControl.touched).toBe(false);

      component.onSubmit();

      expect(authServiceMock.changePassword).not.toHaveBeenCalled();
      expect(component.currentPasswordControl.touched).toBe(true);
      expect(component.newPasswordControl.touched).toBe(true);
      expect(component.confirmPasswordControl.touched).toBe(true);
      expect(component.loading()).toBe(false);
    });

    it('should not submit if loading signal is already true', () => {
      component.passwordForm.setValue({
        currentPassword: 'OldPassword123!',
        newPassword: 'NewSecurePassword456!',
        confirmPassword: 'NewSecurePassword456!'
      });
      component.loading.set(true);

      component.onSubmit();

      expect(authServiceMock.changePassword).not.toHaveBeenCalled();
    });
  });

  describe('Successful Password Change Flow', () => {
    it('should call changePassword, show success toast, logout without navigation, and redirect to login', () => {
      component.passwordForm.setValue({
        currentPassword: 'OldPassword123!',
        newPassword: 'NewSecurePassword456!',
        confirmPassword: 'NewSecurePassword456!'
      });

      authServiceMock.changePassword.mockReturnValue(of(undefined));

      component.onSubmit();

      expect(authServiceMock.changePassword).toHaveBeenCalledWith('OldPassword123!', 'NewSecurePassword456!');
      expect(component.loading()).toBe(false);

      expect(messageServiceMock.add).toHaveBeenCalledWith({
        severity: 'success',
        summary: 'Password updated',
        detail: 'Your password has been changed successfully. Please log in again.',
        life: 5000
      });

      expect(authServiceMock.logout).toHaveBeenCalledWith(false);
      expect(routerMock.login).toHaveBeenCalled();
    });

    it('should notify via UserInfoService when MessageService is not provided', () => {
      const fixtureWithoutMessageService = TestBed.createComponent(SettingsSecurityComponent);
      const compWithoutMessageService = fixtureWithoutMessageService.componentInstance;
      (compWithoutMessageService as unknown as { messageService: unknown }).messageService = null;

      compWithoutMessageService.passwordForm.setValue({
        currentPassword: 'OldPassword123!',
        newPassword: 'NewSecurePassword456!',
        confirmPassword: 'NewSecurePassword456!'
      });

      authServiceMock.changePassword.mockReturnValue(of(undefined));

      compWithoutMessageService.onSubmit();

      expect(userInfoServiceMock.success).toHaveBeenCalledWith(
        'Password updated',
        'Your password has been changed successfully. Please log in again.'
      );
      expect(authServiceMock.logout).toHaveBeenCalledWith(false);
      expect(routerMock.login).toHaveBeenCalled();
    });
  });

  describe('Error Handling and Code Mapping', () => {
    beforeEach(() => {
      component.passwordForm.setValue({
        currentPassword: 'OldPassword123!',
        newPassword: 'NewSecurePassword456!',
        confirmPassword: 'NewSecurePassword456!'
      });
    });

    it('should map INVALID_CURRENT_PASSWORD code to invalid current password error message', () => {
      const httpError = new HttpErrorResponse({
        status: 400,
        error: {
          code: 'INVALID_CURRENT_PASSWORD',
          detail: 'Current password does not match'
        }
      });
      authServiceMock.changePassword.mockReturnValue(throwError(() => httpError));

      component.onSubmit();

      expect(component.loading()).toBe(false);
      expect(authServiceMock.logout).not.toHaveBeenCalled();
      expect(routerMock.login).not.toHaveBeenCalled();
      expect(messageServiceMock.add).toHaveBeenCalledWith({
        severity: 'error',
        summary: 'Password change failed',
        detail: 'The current password you entered is incorrect.',
        life: 5000
      });
    });

    it('should map SAME_PASSWORD code to same password error message', () => {
      const httpError = new HttpErrorResponse({
        status: 400,
        error: {
          code: 'SAME_PASSWORD',
          detail: 'New password must be different from current password'
        }
      });
      authServiceMock.changePassword.mockReturnValue(throwError(() => httpError));

      component.onSubmit();

      expect(component.loading()).toBe(false);
      expect(messageServiceMock.add).toHaveBeenCalledWith({
        severity: 'error',
        summary: 'Password change failed',
        detail: 'The new password must be different from your current password.',
        life: 5000
      });
    });

    it('should map NO_PASSWORD_CREDENTIALS code to no credentials error message', () => {
      const httpError = new HttpErrorResponse({
        status: 400,
        error: {
          code: 'NO_PASSWORD_CREDENTIALS',
          detail: 'User credentials not found'
        }
      });
      authServiceMock.changePassword.mockReturnValue(throwError(() => httpError));

      component.onSubmit();

      expect(component.loading()).toBe(false);
      expect(messageServiceMock.add).toHaveBeenCalledWith({
        severity: 'error',
        summary: 'Password change failed',
        detail: 'Your account does not use password authentication.',
        life: 5000
      });
    });

    it('should map VALIDATION_FAILED code to the validation message, not to wrong current password', () => {
      const httpError = new HttpErrorResponse({
        status: 400,
        error: {
          title: 'VALIDATION_FAILED',
          code: 'VALIDATION_FAILED',
          detail: 'currentPassword: Current password must not be blank'
        }
      });
      authServiceMock.changePassword.mockReturnValue(throwError(() => httpError));

      component.onSubmit();

      expect(component.loading()).toBe(false);
      expect(messageServiceMock.add).toHaveBeenCalledWith({
        severity: 'error',
        summary: 'Password change failed',
        detail: 'The entered passwords are not valid.',
        life: 5000
      });
    });

    it('should ignore the title when no code is present', () => {
      const httpError = new HttpErrorResponse({
        status: 400,
        error: {
          title: 'INVALID_CURRENT_PASSWORD',
          detail: 'Current password does not match'
        }
      });
      authServiceMock.changePassword.mockReturnValue(throwError(() => httpError));

      component.onSubmit();

      expect(messageServiceMock.add).toHaveBeenCalledWith(expect.objectContaining({
        detail: 'An unexpected error occurred while changing your password. Please try again.'
      }));
    });

    it('should not resolve inherited object keys as error codes', () => {
      const httpError = new HttpErrorResponse({ status: 400, error: { code: 'constructor' } });
      authServiceMock.changePassword.mockReturnValue(throwError(() => httpError));

      component.onSubmit();

      expect(messageServiceMock.add).toHaveBeenCalledWith(expect.objectContaining({
        detail: 'An unexpected error occurred while changing your password. Please try again.'
      }));
    });

    it('should not misclassify unknown backend code even if detail contains keywords', () => {
      const httpError = new HttpErrorResponse({
        status: 400,
        error: {
          code: 'UNEXPECTED_VALIDATION_FAILURE',
          detail: 'Failed to process current password due to maintenance'
        }
      });
      authServiceMock.changePassword.mockReturnValue(throwError(() => httpError));

      component.onSubmit();

      expect(component.loading()).toBe(false);
      expect(messageServiceMock.add).toHaveBeenCalledWith({
        severity: 'error',
        summary: 'Password change failed',
        detail: 'An unexpected error occurred while changing your password. Please try again.',
        life: 5000
      });
    });

    it('should not parse English detail text when the code is missing (legacy 400 without code)', () => {
      const httpError = new HttpErrorResponse({
        status: 400,
        error: { detail: 'Current password does not match' }
      });
      authServiceMock.changePassword.mockReturnValue(throwError(() => httpError));

      component.onSubmit();

      expect(messageServiceMock.add).toHaveBeenLastCalledWith(expect.objectContaining({
        detail: 'An unexpected error occurred while changing your password. Please try again.'
      }));
    });

    it.each([
      ['HTTP 500', new HttpErrorResponse({ status: 500, statusText: 'Internal Server Error' })],
      ['network failure (status 0)', new HttpErrorResponse({ status: 0, statusText: 'Unknown Error', error: new ProgressEvent('error') })],
      ['HTTP 401 (expired or invalid JWT)', new HttpErrorResponse({ status: 401, statusText: 'Unauthorized' })],
      ['non-HTTP error', new Error('boom')]
    ])('should show generic error, reset loading and not log out on %s', (_name, error) => {
      authServiceMock.changePassword.mockReturnValue(throwError(() => error));

      component.onSubmit();

      expect(component.loading()).toBe(false);
      expect(messageServiceMock.add).toHaveBeenCalledWith({
        severity: 'error',
        summary: 'Password change failed',
        detail: 'An unexpected error occurred while changing your password. Please try again.',
        life: 5000
      });
      expect(authServiceMock.logout).not.toHaveBeenCalled();
      expect(routerMock.login).not.toHaveBeenCalled();
    });

    it('should keep the entered values so the user can correct them after an error', () => {
      authServiceMock.changePassword.mockReturnValue(
        throwError(() => new HttpErrorResponse({ status: 400, error: { code: 'INVALID_CURRENT_PASSWORD' } }))
      );

      component.onSubmit();

      expect(component.newPasswordControl.value).toBe('NewSecurePassword456!');
      expect(component.passwordForm.valid).toBe(true);
    });

    it('should notify via UserInfoService on error when MessageService is not provided', () => {
      const fixtureWithoutMessageService = TestBed.createComponent(SettingsSecurityComponent);
      const compWithoutMessageService = fixtureWithoutMessageService.componentInstance;
      (compWithoutMessageService as unknown as { messageService: unknown }).messageService = null;

      compWithoutMessageService.passwordForm.setValue({
        currentPassword: 'OldPassword123!',
        newPassword: 'NewSecurePassword456!',
        confirmPassword: 'NewSecurePassword456!'
      });

      const httpError = new HttpErrorResponse({
        status: 400,
        error: { code: 'INVALID_CURRENT_PASSWORD' }
      });
      authServiceMock.changePassword.mockReturnValue(throwError(() => httpError));

      compWithoutMessageService.onSubmit();

      expect(userInfoServiceMock.error).toHaveBeenCalledWith(
        'Password change failed',
        'The current password you entered is incorrect.'
      );
    });
  });
});
