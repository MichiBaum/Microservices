import { Routes } from '@angular/router';
import {AuthenticationComponent} from "./authentication/authentication.component";
import {MicroserviceOverviewComponent} from "./microservice-overview/microservice-overview.component";
import {AboutMeComponent} from "./about-me/about-me.component";
import {hasPermissionGuard, isAuthenticatedGuard, isPermittedGuard} from "./core/guards/auth.guard";
import {HomeComponent} from "./home/home.component";
import {ImprintComponent} from "./imprint/imprint.component";
import {Sides} from "./core/config/sides";

export const routes: Routes = [
  {
    path: '',
    component: HomeComponent,
    // canActivate: [isPermittedGuard]
  },
  {
    path: 'home',
    component: HomeComponent,
    // canActivate: [isPermittedGuard]
  },
  {
    path: 'login',
    component: AuthenticationComponent
  },
  {
    path: 'microservices',
    component: MicroserviceOverviewComponent,
    // canActivate: [isPermittedGuard]
  },
  {
    path: Sides.imprint.navigation,
    component: ImprintComponent
  },
  {
    path: 'about-me',
    component: AboutMeComponent
  }
];
