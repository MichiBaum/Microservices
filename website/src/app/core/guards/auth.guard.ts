import {ActivatedRouteSnapshot, CanActivateFn, RouterStateSnapshot} from '@angular/router';
import {inject} from "@angular/core";
import {AuthService} from "../services/auth.service";
import {PermissionService} from "../services/permission.service";

/**
 * A guard function used to determine if a route can be activated based on the user's authentication status.
 *
 * This function checks if the user is authenticated by leveraging the AuthService. If the user is not authenticated,
 * it triggers a navigation action defined in the AuthService and prevents route activation by returning `false`.
 * If the user is authenticated, it allows the route activation by returning `true`.
 *
 * @param {object} route - The route that is being accessed.
 * @param {object} state - The state of the router at the time the guard is invoked.
 * @return {boolean} Indicates whether the route can be activated.
 */
export const isAuthenticatedGuard: CanActivateFn = (route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean => {
  let authService = inject(AuthService);

  if(!authService.isAuthenticated()){
    authService.navigate()
    return false;
  }
  return authService.isAuthenticated();
};

/**
 * Determines whether a route can be activated based on user authentication
 * and required permissions.
 *
 * @param {ActivatedRouteSnapshot} route - The current route snapshot.
 * @param {RouterStateSnapshot} state - The current RouterState snapshot.
 * @returns {boolean} Whether the route can be activated.
 */
export const isPermittedGuard: CanActivateFn = (route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean => {
  let authService = inject(AuthService);

  if(!authService.isAuthenticated())
    return false;

  let permissionService = inject(PermissionService);
  return permissionService.hasAnyOf(route.data['permissions'])
};
