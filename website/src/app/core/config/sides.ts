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
    canActivate: []
  } as Side,
  microservices: {
    name: "microservices",
    translationKey: "",
    navigation: "microservices",
    canActivate: [isAuthenticatedGuard]
  } as Side,
  about_me: {
    name: "about-me",
    translationKey: "",
    navigation: "about-me",
    canActivate: []
  } as Side,
  login: {
    name: "login",
    translationKey: "login.title",
    navigation: "login",
    canActivate: []
  } as Side,
  home: {
    name: "home",
    translationKey: "home.title",
    navigation: "home",
    canActivate: [isAuthenticatedGuard]
  } as Side,
  chess: {
    name: "chess",
    translationKey: "chess.title",
    navigation: "chess",
    canActivate: [isAuthenticatedGuard]
  } as Side,
  default: {
    name: "default",
    translationKey: "",
    navigation: "",
    canActivate: [isPermittedGuard]
  } as Side
}
