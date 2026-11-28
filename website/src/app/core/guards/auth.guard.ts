import {ActivatedRouteSnapshot, CanActivateFn, Router, RouterStateSnapshot, UrlTree} from '@angular/router';
import {inject} from "@angular/core";
import {AuthService} from "../api-services/auth.service";
import {PermissionService} from "../services/permission.service";

/**
 * A guard function used to determine if a route can be activated based on the user's authentication status.
 *
 * If the user is not authenticated, the nearest `unauthenticatedRedirect` value found in the route's ancestors
 * (including the route itself) is used as the redirect target and returned as a `UrlTree`. If no ancestor defines
 * it, the AuthService navigation behavior is used and the route is not activated. Authenticated users can activate the route.
 *
 * @param {ActivatedRouteSnapshot} route - The route that is being accessed.
 * @return {boolean | UrlTree} Indicates whether the route can be activated or redirects to another route.
 */
export const isAuthenticatedGuard: CanActivateFn = (route: ActivatedRouteSnapshot): boolean | UrlTree => {
  const authService = inject(AuthService);

  if (!authService.isAuthenticated()) {
    const redirect: string | undefined = route.pathFromRoot
      .map(snapshot => snapshot.data['unauthenticatedRedirect'])
      .reverse()
      .find(value => !!value);

    if (redirect) {
      return inject(Router).parseUrl(redirect);
    }

    authService.navigate();
    return false;
  }
  return true;
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
