import {Permissions} from "./permissions";
import {PermissionService} from "../services/permission.service";

interface Side{
  name: string
  translationKey: string,
  navigation: string,
  canActivate: (service: PermissionService) => boolean,
  neededPermissions: Permissions[]
}

export const Sides = {
  imprint : {
    name: "imprint",
    translationKey: "imprint.title",
    navigation: "imprint",
    canActivate: (service: PermissionService) => true,
    neededPermissions: []
  } as Side,
  microservices: {
    name: "microservices",
    translationKey: "microservices.title",
    navigation: "microservices",
    canActivate: (service: PermissionService) => service.hasAnyOf([Permissions.ADMIN_SERVICE]),
    neededPermissions: [Permissions.ADMIN_SERVICE]
  } as Side,
  about_me: {
    name: "about-me",
    translationKey: "about-me.title",
    navigation: "about-me",
    canActivate: (service: PermissionService) => true,
    neededPermissions: []
  } as Side,
  login: {
    name: "login",
    translationKey: "login.title",
    navigation: "login",
    canActivate: (service: PermissionService) => !service.isAuthenticated(),
    neededPermissions: []
  } as Side,
  register: {
    name: "register",
    translationKey: "register.title",
    navigation: "register",
    canActivate: (service: PermissionService) => !service.isAuthenticated(),
    neededPermissions: []
  } as Side,
  home: {
    name: "home",
    translationKey: "home.title",
    navigation: "home",
    canActivate: (service: PermissionService) => true,
    neededPermissions: []
  } as Side,
  chess: {
    name: "chess",
    translationKey: "chess.title",
    navigation: "chess",
    canActivate: (service: PermissionService) => service.hasAnyOf([Permissions.CHESS_SERVICE]),
    neededPermissions: [Permissions.CHESS_SERVICE]
  } as Side,
  chess_settings: {
    name: "chess-settings",
    translationKey: "chess-settings.title",
    navigation: "chess-settings",
    canActivate: (service: PermissionService) => service.hasAnyOf([Permissions.CHESS_SERVICE]),
    neededPermissions: [Permissions.CHESS_SERVICE_ADMIN]
  } as Side,
  default: {
    name: "default",
    translationKey: "",
    navigation: "",
    canActivate: (service: PermissionService) => service.isAuthenticated(),
    neededPermissions: []
  } as Side,
  donate:{
    name: "donate",
    translationKey: "donate.title",
    navigation: "donate",
    canActivate: (service: PermissionService) => true,
    neededPermissions: []
  } as Side,
  fitness:{
    name: "fitness",
    translationKey: "fitness.title",
    navigation: "fitness",
    canActivate: (service: PermissionService) => service.hasAnyOf([Permissions.FITNESS_SERVICE]),
    neededPermissions: [Permissions.FITNESS_SERVICE]
  } as Side,
  fitness_settings:{
    name: "fitness-settings",
    translationKey: "fitness-settings.title",
    navigation: "fitness-settings",
    canActivate: (service: PermissionService) => service.hasAnyOf([Permissions.FITNESS_SERVICE]),
    neededPermissions: [Permissions.FITNESS_SERVICE]
  } as Side,
  music:{
    name: "music",
    translationKey: "music.title",
    navigation: "music",
    canActivate: (service: PermissionService) => service.hasAnyOf([Permissions.MUSIC_SERVICE]),
    neededPermissions: [Permissions.MUSIC_SERVICE]
  } as Side,
  music_settings:{
    name: "music-settings",
    translationKey: "music-settings.title",
    navigation: "music-settings",
    canActivate: (service: PermissionService) => service.hasAnyOf([Permissions.MUSIC_SERVICE]),
    neededPermissions: [Permissions.MUSIC_SERVICE]
  } as Side
}
