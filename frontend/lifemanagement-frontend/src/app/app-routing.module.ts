import {NgModule} from '@angular/core';
import {Route, RouterModule, Routes} from '@angular/router';
import {BackendDocumentationComponent} from './backend-documentation/backend-documentation.component';
import {AuthGuardService as AuthGuard} from './core/services/auth-guard.service';
import {FrontendDocumentationComponent} from './frontend-documentation/frontend-documentation.component';
import {HomeComponent} from './home/home.component';
import {ImprintComponent} from './imprint/imprint.component';
import {LoginComponent} from './login/login.component';
import {LoggingComponent} from './logs/logging.component';
import {PrivacyPolicyComponent} from './privacy-policy/privacy-policy.component';
import {UsersettingsComponent} from './usersettings/usersettings.component';

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
  // tslint:disable-next-line:max-line-length
  imports: [RouterModule.forRoot(routes)], // TODO , { preloadingStrategy: PreloadAllModules } https://medium.com/angular-in-depth/network-aware-preloading-strategy-for-angular-lazy-loading-b883a0fbbaf0  https://vsavkin.com/angular-router-preloading-modules-ba3c75e424cb
  exports: [RouterModule]
})
export class AppRoutingModule { }
