import {EventEmitter, inject, Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {CookieService} from "ngx-cookie-service";
import {ActivatedRouteSnapshot, Router} from "@angular/router";
import {Authentication, AuthenticationResponse} from "../models/authentication.model";
import {environment} from "../../../environments/environment";
import {Sides} from "../config/sides";
import {RouterNavigationService} from "./router-navigation.service";
import {PermissionService} from "./permission.service";
import {Subject} from "rxjs";

@Injectable({providedIn: 'root'})
export class AuthService {

  successLoginEmitter = new Subject<void>();

  constructor(private http: HttpClient, private router:Router, private routerNavigationService: RouterNavigationService) {
  }

  login(username:string, password:string ) {
    let autentication = {username: username, password: password} as Authentication
    this.http.post<AuthenticationResponse>(environment.authenticationService + '/authenticate', autentication)
      .subscribe(value => {
        if(value.jwt != null && value.jwt !== ""){
          console.log("Successful authentication -> jwt saved in localstorage");
          localStorage.setItem('Authentication', value.jwt);
          this.successLoginEmitter.next()
          this.router.navigate(["/home"])
        }
      })
  }


  getJwtTokenFromLocalStorage(){
    return localStorage.getItem('Authentication')
  }

  jwtIsPresent() {
    const jwt = this.getJwtTokenFromLocalStorage();
    return !(jwt == null || jwt === "");

  }

  isAuthenticated(route: ActivatedRouteSnapshot) {
    let jwtIsPresent = this.jwtIsPresent();

    if (!jwtIsPresent) {
      this.routerNavigationService.login()
      return false;
    }

    return true;
  }
}
