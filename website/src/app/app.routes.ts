import { Routes } from '@angular/router';
import {AuthenticationComponent} from "./authentication/authentication.component";
import {MicroserviceOverviewComponent} from "./microservice-overview/microservice-overview.component";
import {AboutMeComponent} from "./about-me/about-me.component";
import {HomeComponent} from "./home/home.component";
import {ImprintComponent} from "./imprint/imprint.component";
import {Sides} from "./core/config/sides";

export const routes: Routes = [
  {
    path: Sides.default.navigation,
    component: HomeComponent,
    canActivate: Sides.default.canActivate
  },
  {
    path: Sides.home.navigation,
    component: HomeComponent,
    canActivate: Sides.home.canActivate
  },
  {
    path: Sides.login.navigation,
    component: AuthenticationComponent,
    canActivate: Sides.login.canActivate
  },
  {
    path: Sides.microservices.navigation,
    component: MicroserviceOverviewComponent,
    canActivate: Sides.microservices.canActivate
  },
  {
    path: Sides.imprint.navigation,
    component: ImprintComponent,
    canActivate: Sides.imprint.canActivate
  },
  {
    path: Sides.about_me.navigation,
    component: AboutMeComponent,
    canActivate: Sides.about_me.canActivate
  }
];
