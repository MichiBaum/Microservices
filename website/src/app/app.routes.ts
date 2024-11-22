import {Routes} from '@angular/router';
import {LoginComponent} from "./login/login.component";
import {MicroserviceOverviewComponent} from "./microservice-overview/microservice-overview.component";
import {AboutMeComponent} from "./about-me/about-me.component";
import {HomeComponent} from "./home/home.component";
import {ImprintComponent} from "./imprint/imprint.component";
import {Sides} from "./core/config/sides";
import {ChessComponent} from "./chess/chess.component";
import {DonateComponent} from "./donate/donate.component";
import {RegisterComponent} from "./register/register.component";
import {FitnessComponent} from "./fitness/fitness.component";
import {MusicComponent} from "./music/music.component";
import {ChessSettingsComponent} from "./chess-settings/chess-settings.component";
import {FitnessSettingsComponent} from "./fitness-settings/fitness-settings.component";
import {MusicSettingsComponent} from "./music-settings/music-settings.component";

export const routes: Routes = [
  {
    path: Sides.default.navigation,
    component: HomeComponent,
    canActivate: Sides.default.routeCanActivate,
    data: {permissions: Sides.default.neededPermissions}
  },
  {
    path: Sides.home.navigation,
    component: HomeComponent,
    canActivate: Sides.home.routeCanActivate,
    data: {permissions: Sides.home.neededPermissions}
  },
  {
    path: Sides.login.navigation,
    component: LoginComponent,
    canActivate: Sides.login.routeCanActivate,
    data: {permissions: Sides.login.neededPermissions}
  },
  {
    path: Sides.register.navigation,
    component: RegisterComponent,
    canActivate: Sides.register.routeCanActivate,
    data: {permissions: Sides.register.neededPermissions}
  },
  {
    path: Sides.microservices.navigation,
    component: MicroserviceOverviewComponent,
    canActivate: Sides.microservices.routeCanActivate,
    data: {permissions: Sides.microservices.neededPermissions}
  },
  {
    path: Sides.imprint.navigation,
    component: ImprintComponent,
    canActivate: Sides.imprint.routeCanActivate,
    data: {permissions: Sides.imprint.neededPermissions}
  },
  {
    path: Sides.about_me.navigation,
    component: AboutMeComponent,
    canActivate: Sides.about_me.routeCanActivate,
    data: {permissions: Sides.about_me.neededPermissions}
  },
  {
    path: Sides.chess.navigation,
    component: ChessComponent,
    canActivate: Sides.chess.routeCanActivate,
    data: {permissions: Sides.chess.neededPermissions}
  },
  {
    path: Sides.chess_settings.navigation,
    component: ChessSettingsComponent,
    canActivate: Sides.chess_settings.routeCanActivate,
    data: {permissions: Sides.chess_settings.neededPermissions}
  },
  {
    path: Sides.donate.navigation,
    component: DonateComponent,
    canActivate: Sides.donate.routeCanActivate,
    data: {permissions: Sides.donate.neededPermissions}
  },
  {
    path: Sides.fitness.navigation,
    component: FitnessComponent,
    canActivate: Sides.fitness.routeCanActivate,
    data: {permissions: Sides.fitness.neededPermissions}
  },
  {
    path: Sides.fitness_settings.navigation,
    component: FitnessSettingsComponent,
    canActivate: Sides.fitness_settings.routeCanActivate,
    data: {permissions: Sides.fitness_settings.neededPermissions}
  },
  {
    path: Sides.music.navigation,
    component: MusicComponent,
    canActivate: Sides.music.routeCanActivate,
    data: {permissions: Sides.music.neededPermissions}
  },
  {
    path: Sides.music_settings.navigation,
    component: MusicSettingsComponent,
    canActivate: Sides.music_settings.routeCanActivate,
    data: {permissions: Sides.music_settings.neededPermissions}
  }
];
