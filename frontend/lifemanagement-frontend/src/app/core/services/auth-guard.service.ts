import {Injectable} from '@angular/core';
import {CanActivate} from '@angular/router';
import {AuthService} from './auth.service';
import {RouternavigationService} from './routernavigation.service';

@Injectable()
export class AuthGuardService implements CanActivate {

  constructor(public auth: AuthService, public routernavigationService: RouternavigationService) {}

  canActivate = (): boolean => {
    if (!this.auth.isLoggedIn()) {
      this.routernavigationService.loginNavigate();
      return false;
    }
    return true;
  }
}
