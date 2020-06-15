import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import { AuthGuardService as AuthGuard } from './core/services/auth-guard.service';
import {HomeComponent} from './home/home.component';
import {ImprintComponent} from './imprint/imprint.component';
import {LoginComponent} from './login/login.component';
import {LoggingComponent} from './logs/logging.component';
import {UsersettingsComponent} from './usersettings/usersettings.component';

const routes: Routes = [
  {
    path: 'login',
    component: LoginComponent
  }, {
    path: 'home',
    component: HomeComponent,
    canActivate: [AuthGuard]
  }, {
    path: '',
    component: HomeComponent,
    canActivate: [AuthGuard]
  }, {
    path: 'logmanagement',
    component: LoggingComponent,
    canActivate: [AuthGuard]
  }, {
    path: 'usersettings',
    component: UsersettingsComponent,
    canActivate: [AuthGuard]
  }, {
    path: 'imprint',
    component: ImprintComponent,
    canActivate: [AuthGuard]
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
