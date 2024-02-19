import { Routes } from '@angular/router';
import {AuthenticationComponent} from "./authentication/authentication.component";
import {MicroserviceOverviewComponent} from "./microservice-overview/microservice-overview.component";

export const routes: Routes = [
  {
    path: 'authentication',
    component: AuthenticationComponent
  },
  {
    path: 'microservices',
    component: MicroserviceOverviewComponent
  }
];
