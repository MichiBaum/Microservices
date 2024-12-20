import { Injectable, inject } from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {Authentication, AuthenticationResponse} from "../models/authentication.model";
import {environment} from "../../../environments/environment";
import {RouterNavigationService} from "./router-navigation.service";
import {catchError, Subject} from "rxjs";
import {HttpErrorHandler} from "../config/http-error-handler.service";
import {UserInfoService} from "./user-info.service";

@Injectable({providedIn: 'root'})
export class AuthService {
  private http = inject(HttpClient);
  private router = inject(RouterNavigationService);
  private httpErrorConfig = inject(HttpErrorHandler);
  private userInfoService = inject(UserInfoService);

  successLoginEmitter = new Subject<void>();
  logoutEmitter = new Subject<void>()

  login(username:string, password:string ) {
    let autentication = {username: username, password: password} as Authentication
    this.http.post<AuthenticationResponse>(environment.authenticationService + '/authenticate', autentication)
      .pipe(catchError(err => this.httpErrorConfig.handleError(err, this.userInfoService)))
      .subscribe(value => {
        if(value.jwt != null && value.jwt !== ""){
          localStorage.setItem('Authentication', value.jwt);
          this.successLoginEmitter.next()
          this.router.home()
        }
      })
  }

  logout(){
    localStorage.removeItem('Authentication');
    this.http.post(environment.authenticationService + '/logout', {})
      .pipe(catchError(err => this.httpErrorConfig.handleError(err, this.userInfoService)))
      .subscribe();
    this.logoutEmitter.next();
    this.router.home()
  }

  getJwtTokenFromLocalStorage(){
    return localStorage.getItem('Authentication')
  }

  jwtIsPresent() {
    const jwt = this.getJwtTokenFromLocalStorage();
    return !(jwt == null || jwt === "");

  }

  isAuthenticated() {
    return this.jwtIsPresent();
  }

  navigate() {
    this.router.home()
  }
}
