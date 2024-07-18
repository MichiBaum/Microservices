import { CanActivateFn } from '@angular/router';
import {inject} from "@angular/core";
import {AuthService} from "../services/auth.service";

export const isAuthenticatedGuard: CanActivateFn = (route, state) => {
  let authService = inject(AuthService);
  return authService.isAuthenticated(route);
};

export const hasPermissionGuard: CanActivateFn = (route, state) => {

  return true;
};

export const isPermittedGuard: CanActivateFn = (route, state) => {
  return isAuthenticatedGuard(route, state) && hasPermissionGuard(route, state)
};
