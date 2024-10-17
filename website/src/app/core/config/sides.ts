import {isAuthenticatedGuard, isPermittedGuard} from "../guards/auth.guard";
import {CanActivateFn} from "@angular/router";
import {Permissions} from "./permissions";
import {PermissionService} from "../services/permission.service";

interface Side{
  name: string
  translationKey: string,
  navigation: string,
  canActivate: (service: PermissionService) => boolean,
  routeCanActivate: Array<CanActivateFn>
}

export const Sides = {
  imprint : {
    name: "imprint",
    translationKey: "imprint.title",
    navigation: "imprint",
    canActivate: (service: PermissionService) => true,
    routeCanActivate: []
  } as Side,
  microservices: {
    name: "microservices",
    translationKey: "microservices.title",
    navigation: "microservices",
    canActivate: (service: PermissionService) => service.hasAnyOf([Permissions.ADMIN_SERVICE]),
    routeCanActivate: [isPermittedGuard]
  } as Side,
  about_me: {
    name: "about-me",
    translationKey: "about-me.title",
    navigation: "about-me",
    canActivate: (service: PermissionService) => true,
    routeCanActivate: []
  } as Side,
  login: {
    name: "login",
    translationKey: "login.title",
    navigation: "login",
    canActivate: (service: PermissionService) => !service.isAuthenticated(),
    routeCanActivate: []
  } as Side,
  home: {
    name: "home",
    translationKey: "home.title",
    navigation: "home",
    canActivate: (service: PermissionService) => service.isAuthenticated(),
    routeCanActivate: [isAuthenticatedGuard]
  } as Side,
  chess: {
    name: "chess",
    translationKey: "chess.title",
    navigation: "chess",
    canActivate: (service: PermissionService) => service.isAuthenticated(),
    routeCanActivate: [isAuthenticatedGuard]
  } as Side,
  default: {
    name: "default",
    translationKey: "",
    navigation: "",
    canActivate: (service: PermissionService) => service.isAuthenticated(),
    routeCanActivate: [isAuthenticatedGuard]
  } as Side
}
