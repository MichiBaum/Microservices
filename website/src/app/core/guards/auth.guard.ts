import { CanActivateFn } from '@angular/router';
import {inject} from "@angular/core";
import {AuthService} from "../services/auth.service";
import {PermissionService} from "../services/permission.service";

export const isAuthenticatedGuard: CanActivateFn = (route, state) => {
  let authService = inject(AuthService);
  return authService.isAuthenticated(route);
};

export const hasPermissionGuard: CanActivateFn = (route, state) => {
  // TODO get jwt && look at permissions
  // inject(PermissionsService).canActivate(inject(UserToken), route. params['id']);
  inject(PermissionService).canActivate(route.params['id'])
  return true;
};

export const isPermittedGuard: CanActivateFn = (route, state) => {
  return isAuthenticatedGuard(route, state) && hasPermissionGuard(route, state)
};
