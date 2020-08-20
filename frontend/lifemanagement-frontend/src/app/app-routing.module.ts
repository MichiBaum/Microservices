import {NgModule} from '@angular/core';
import {Route, RouterModule, Routes} from '@angular/router';
import { AuthGuardService as AuthGuard } from './core/services/auth-guard.service';
import {FrontendDocumentationComponent} from './frontend-documentation/frontend-documentation.component';
import {HomeComponent} from './home/home.component';
import {ImprintComponent} from './imprint/imprint.component';
import {LoginComponent} from './login/login.component';
import {LoggingComponent} from './logs/logging.component';
import {PrivacyPolicyComponent} from './privacy-policy/privacy-policy.component';
import {UsersettingsComponent} from './usersettings/usersettings.component';
import {BackendDocumentationComponent} from "./backend-documentation/backend-documentation.component";

/**
 * All own Routes
 */
const routes: Routes = [
  {
    path: 'login',
    component: LoginComponent
  } as Route, {
    path: 'home',
    component: HomeComponent,
    canActivate: [AuthGuard]
  } as Route, {
    path: '',
    component: HomeComponent,
    canActivate: [AuthGuard]
  } as Route, {
    path: 'logmanagement',
    component: LoggingComponent,
    canActivate: [AuthGuard]
  } as Route, {
    path: 'usersettings',
    component: UsersettingsComponent,
    canActivate: [AuthGuard]
  } as Route, {
    path: 'imprint',
    component: ImprintComponent
  } as Route, {
    path: 'privacy-policy',
    component: PrivacyPolicyComponent
  } as Route, {
    path: 'frontend-documentation',
    component: FrontendDocumentationComponent
  } as Route, {
    path: 'backend-documentation',
    component: BackendDocumentationComponent
  } as Route
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
