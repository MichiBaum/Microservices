import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {tap} from 'rxjs/internal/operators/tap';
import {PermissionEnum} from '../models/enum/permission.enum';
import {IJWT} from '../models/jwt.model';
import {ApiService} from './api.service';
import {RouternavigationService} from './routernavigation.service';
import {SessionStorageService} from './session-storage.service';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private activeToken: IJWT;

  constructor(
    private apiService: ApiService,
    private routernavigationService: RouternavigationService,
    private sessionStorageService: SessionStorageService
  ) {
    this.activeToken = JSON.parse(this.sessionStorageService.getValue('jwt'));
  }

  login = (username: string, password: string): Observable<any> => {
    return this.apiService.postAll('/login', {username, password})
      .pipe(tap((res) => this.setSession(res)));
  }

  private setSession = (authResult: IJWT): void => {
    if (authResult.token) {
      this.activeToken = authResult;
      this.sessionStorageService.setKeyAndValue('jwt', JSON.stringify(authResult));
    }
  }

  logout = (): void => {
    this.sessionStorageService.removeItem('jwt');
    this.activeToken = null;
    this.routernavigationService.loginNavigate();
  }

  public isLoggedIn = (): boolean => {
    if (!this.activeToken) { return false; }
    return Date.now() < this.activeToken.expiresAt;
  }

  public getTokenHeaderName = (): string => {
    if (!this.activeToken) { return ''; }
    return this.activeToken.headerName;
  }

  public getTokenValue = (): string => {
    if (!this.activeToken) { return ''; }
    return this.activeToken.token;
  }

  public hasAnyPermission = (permissionsNeeded: PermissionEnum[]): boolean => {
    if (permissionsNeeded.length === 0) { return true; }

    if (this.activeToken == null || this.activeToken.permissions == null) {
      return false;
    }

    for (const needed of permissionsNeeded) {
      for (const owned of this.activeToken.permissions) {
        if (needed === owned) {
          return true;
        }
      }
    }
    return false;
  }

  public getUsername = (): string => {
    if (!this.activeToken) { return ''; }
    return this.activeToken.username;
  }

}
