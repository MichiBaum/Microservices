import {isAuthenticatedGuard, isPermittedGuard} from "../guards/auth.guard";
import {CanActivateFn} from "@angular/router";

interface Side{
  canActivate: Array<CanActivateFn>;
  name: string
  translationKey: string,
  navigation: string
}

export const Sides = {
  imprint : {
    name: "imprint",
    translationKey: "imprint.title",
    navigation: "imprint",
    canActivate: [],
    needsPermission: []
  } as Side,
  microservices: {
    name: "microservices",
    translationKey: "",
    navigation: "microservices",
    canActivate: [isPermittedGuard],
    needsPermission: []
  } as Side,
  about_me: {
    name: "about-me",
    translationKey: "",
    navigation: "about-me",
    canActivate: [],
    needsPermission: []
  } as Side,
  login: {
    name: "login",
    translationKey: "login.title",
    navigation: "login",
    canActivate: [],
    needsPermission: []
  } as Side,
  home: {
    name: "home",
    translationKey: "home.title",
    navigation: "home",
    canActivate: [isAuthenticatedGuard],
    needsPermission: []
  } as Side,
  chess: {
    name: "chess",
    translationKey: "chess.title",
    navigation: "chess",
    canActivate: [isAuthenticatedGuard],
    needsPermission: []
  } as Side,
  default: {
    name: "default",
    translationKey: "",
    navigation: "",
    canActivate: [isAuthenticatedGuard],
    needsPermission: []
  } as Side
}
