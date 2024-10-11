import {isPermittedGuard} from "../guards/auth.guard";
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
    translationKey: "",
    navigation: "imprint",
    canActivate: []
  } as Side,
  microservices: {
    name: "microservices",
    translationKey: "",
    navigation: "microservices",
    canActivate: [isPermittedGuard]
  } as Side,
  about_me: {
    name: "about-me",
    translationKey: "",
    navigation: "about-me",
    canActivate: []
  } as Side,
  login: {
    name: "login",
    translationKey: "",
    navigation: "login",
    canActivate: []
  } as Side,
  home: {
    name: "home",
    translationKey: "",
    navigation: "home",
    canActivate: [isPermittedGuard]
  } as Side,
  chess: {
    name: "chess",
    translationKey: "",
    navigation: "chess",
    canActivate: []
  } as Side,
  default: {
    name: "default",
    translationKey: "",
    navigation: "",
    canActivate: [isPermittedGuard]
  } as Side
}
