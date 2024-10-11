import {inject, Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {CookieService} from "ngx-cookie-service";
import {ActivatedRouteSnapshot, Router} from "@angular/router";
import {Authentication, AuthenticationResponse} from "../models/authentication.model";
import {environment} from "../../../environments/environment";
import {Sides} from "../config/sides";

@Injectable({providedIn: 'root'})
export class AuthService {

  constructor(private http: HttpClient, private router:Router) {
  }

  login(username:string, password:string ) {
    let autentication = {username: username, password: password} as Authentication
    this.http.post<AuthenticationResponse>(environment.authenticationService + '/authenticate', autentication)
      .subscribe(value => {
        if(value.jwt != null && value.jwt !== ""){
          console.log("Successful authentication -> jwt saved in localstorage");
          localStorage.setItem('Authentication', value.jwt);
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
      this.router.navigate(['/login']);
      return false;
    }

    return true;
  }
}
