import {inject, Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {Authentication, AuthenticationResponse, Register, RegisterResponse} from "../models/authentication.model";
import {RouterNavigationService} from "../services/router-navigation.service";
import {catchError, Observable, Subject} from "rxjs";
import {CustomErrorMatching, HttpErrorHandler} from "../config/http-error-handler.service";
import {UserInfoService} from "../services/user-info.service";
import {EnvironmentConfig} from "../config/environment.config";
import {JwtPayload} from "../models/jwtPayload.model";
import {jwtDecode} from "jwt-decode";

@Injectable({providedIn: 'root'})
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly environment = inject(EnvironmentConfig);
  private readonly router = inject(RouterNavigationService);
  private readonly httpErrorConfig = inject(HttpErrorHandler);
  private readonly userInfoService = inject(UserInfoService);
  private readonly jwtName = 'Authentication';

  successLoginEmitter = new Subject<void>();
  logoutEmitter = new Subject<void>()

  login(username:string, password:string ) {
    let autentication = {username: username, password: password} as Authentication
    this.http.post<AuthenticationResponse>(this.environment.authenticationService() + '/authenticate', autentication)
      .subscribe(value => {
        if(value.jwt != null && value.jwt !== ""){
          localStorage.setItem(this.jwtName, value.jwt);
          this.successLoginEmitter.next()
          this.router.home()
        }
      })
  }

  register(registerUser: Register, customErrorMatching: CustomErrorMatching): Observable<RegisterResponse> {
    return this.http.post<RegisterResponse>(this.environment.authenticationService() + '/register', registerUser)
      .pipe(catchError(err => this.httpErrorConfig.handleError(err, this.userInfoService, customErrorMatching)))
  }

  isTokenExpired(jwtString: string){
    const jwt = jwtDecode<JwtPayload>(jwtString);
    return jwt.exp * 1000 < Date.now();

  }

  logoutIfTokenExpired(){
    let jwtTokenFromLocalStorage = this.getJwtTokenFromLocalStorage();
    if(jwtTokenFromLocalStorage == null){
      return;
    }
    if(this.isTokenExpired(jwtTokenFromLocalStorage)){
      this.logout();
    }
  }

  logout(){
    localStorage.removeItem(this.jwtName);
    this.logoutEmitter.next();
    this.router.home()
  }

  getJwtTokenFromLocalStorage(){
    return localStorage.getItem(this.jwtName)
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

    private getCookieDomain(): string {
        const hostname = window.location.hostname;
        return hostname.startsWith('.') ? hostname : '.' + hostname;
    }

    setJwtAsCookie() {
        const jwt = this.getJwtTokenFromLocalStorage();
        if (!jwt) {
            return;
        }

        const decodedJwt = jwtDecode<JwtPayload>(jwt);
        const jwtExpirationDate = new Date(decodedJwt.exp * 1000);
        const maxCookieExpiration = new Date(Date.now() + (2 * 60 * 60 * 1000)); // Current time + 2 hours

        // Ensure the cookie never exceeds the JWT's valid expiration
        const expires = jwtExpirationDate < maxCookieExpiration ? jwtExpirationDate : maxCookieExpiration;
        const domain = this.getCookieDomain();
        document.cookie = `${this.jwtName}=${jwt}; ${expires}; domain=${domain}; path=/`;

    }

    removeAuthCookie() {
        const domain = this.getCookieDomain();
        document.cookie = `${this.jwtName}=; expires=Thu, 01 Jan 1970 00:00:01 GMT; domain=${domain}; path=/`;
    }
}
