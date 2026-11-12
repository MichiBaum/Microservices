import { describe, it, expect, beforeEach, afterEach, vi } from 'vitest';
import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { AUTH_JWT_KEY, AuthService } from './auth.service';
import { RouterNavigationService } from '../services/router-navigation.service';
import { EnvironmentConfig } from '../config/environment.config';
import { HttpErrorHandler } from '../config/http-error-handler.service';
import { UserInfoService } from '../services/user-info.service';

function createJwt(expiresInSeconds = 3600): string {
  const encode = (value: object) => btoa(JSON.stringify(value)).replace(/=+$/, '').replace(/\+/g, '-').replace(/\//g, '_');
  const exp = Math.floor(Date.now() / 1000) + expiresInSeconds;
  return `${encode({ alg: 'none', typ: 'JWT' })}.${encode({ sub: 'user', exp })}.signature`;
}

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;
  let routerMock: { home: ReturnType<typeof vi.fn>; login: ReturnType<typeof vi.fn> };

  const authCookiePresent = () => document.cookie.split(';').some(c => c.trim().startsWith(`${AUTH_JWT_KEY}=`) && c.trim() !== `${AUTH_JWT_KEY}=`);

  beforeEach(() => {
    localStorage.clear();
    routerMock = { home: vi.fn(), login: vi.fn() };

    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: RouterNavigationService, useValue: routerMock },
        { provide: EnvironmentConfig, useValue: { authenticationService: () => 'http://auth.test/api' } },
        { provide: HttpErrorHandler, useValue: { handleError: vi.fn() } },
        { provide: UserInfoService, useValue: { success: vi.fn(), error: vi.fn() } }
      ]
    });

    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
    service.removeAuthCookie();
    localStorage.clear();
  });

  describe('changePassword', () => {
    it('should POST current and new password to the change-password endpoint', () => {
      let completed = false;
      service.changePassword('OldPassword123!', 'NewSecurePassword456!').subscribe({ complete: () => (completed = true) });

      const req = httpMock.expectOne('http://auth.test/api/basic-authentication/change-password');
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual({ currentPassword: 'OldPassword123!', newPassword: 'NewSecurePassword456!' });
      req.flush(null, { status: 204, statusText: 'No Content' });

      expect(completed).toBe(true);
    });
  });

  describe('logout', () => {
    beforeEach(() => {
      localStorage.setItem(AUTH_JWT_KEY, createJwt());
      service.setJwtAsCookie();
    });

    it('should seed a JWT in localStorage and in the auth cookie (test precondition)', () => {
      expect(service.getJwtTokenFromLocalStorage()).not.toBeNull();
      expect(authCookiePresent()).toBe(true);
    });

    it('should remove the stored JWT and the auth cookie, emit logoutEmitter and not redirect home when navigate=false', () => {
      const emitted = vi.fn();
      service.logoutEmitter.subscribe(emitted);

      service.logout(false);

      expect(localStorage.getItem(AUTH_JWT_KEY)).toBeNull();
      expect(service.isAuthenticated()).toBe(false);
      expect(authCookiePresent()).toBe(false);
      expect(emitted).toHaveBeenCalledTimes(1);
      expect(routerMock.home).not.toHaveBeenCalled();
    });

    it('should redirect home by default', () => {
      service.logout();

      expect(localStorage.getItem(AUTH_JWT_KEY)).toBeNull();
      expect(authCookiePresent()).toBe(false);
      expect(routerMock.home).toHaveBeenCalledTimes(1);
    });
  });
});
