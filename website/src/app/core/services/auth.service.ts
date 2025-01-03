import {inject, Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {Authentication, AuthenticationResponse, Register, RegisterResponse} from "../models/authentication.model";
import {environment} from "../../../environments/environment";
import {RouterNavigationService} from "./router-navigation.service";
import {catchError, Observable, Subject} from "rxjs";
import {CustomErrorMatching, HttpErrorHandler} from "../config/http-error-handler.service";
import {UserInfoService} from "./user-info.service";

@Injectable({providedIn: 'root'})
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly router = inject(RouterNavigationService);
  private readonly httpErrorConfig = inject(HttpErrorHandler);
  private readonly userInfoService = inject(UserInfoService);

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

  register(registerUser: Register, customErrorMatching: CustomErrorMatching | undefined): Observable<RegisterResponse> {
    return this.http.post<RegisterResponse>(environment.authenticationService + '/register', registerUser)
      .pipe(catchError(err => this.httpErrorConfig.handleError(err, this.userInfoService, customErrorMatching)))
  }

  logout(){
    localStorage.removeItem('Authentication');
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
