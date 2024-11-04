import {CanActivateFn} from '@angular/router';
import {inject} from "@angular/core";
import {AuthService} from "../services/auth.service";
import {PermissionService} from "../services/permission.service";

export const isAuthenticatedGuard: CanActivateFn = (route, state) => {
  let authService = inject(AuthService);

  if(!authService.isAuthenticated()){
    authService.navigate()
    return false;
  }
  return authService.isAuthenticated();
};

export const isPermittedGuard: CanActivateFn = (route, state) => {
  let authService = inject(AuthService);

  if(!authService.isAuthenticated())
    return false;

  let permissionService = inject(PermissionService);
  return permissionService.hasAnyOf(route.data['permissions'])
};
