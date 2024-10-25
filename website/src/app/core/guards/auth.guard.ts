import {CanActivateFn} from '@angular/router';
import {inject} from "@angular/core";
import {AuthService} from "../services/auth.service";
import {PermissionService} from "../services/permission.service";

export const isAuthenticatedGuard: CanActivateFn = (route, state) => {
  let authService = inject(AuthService);
  return authService.isAuthenticated();
};

export const hasPermissionGuard: CanActivateFn = (route, state) => {
  // TODO get jwt && look at permissions
  let permissionService = inject(PermissionService);
  return permissionService.hasAnyOf(route.data['permissions'])
};

export const isPermittedGuard: CanActivateFn = (route, state) => {
  const isAuthenticated = isAuthenticatedGuard(route, state)
  if(!isAuthenticated){
    return false
  }

  return hasPermissionGuard(route, state)
};
