import {inject, Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {CookieService} from "ngx-cookie-service";
import {ActivatedRouteSnapshot, Router} from "@angular/router";
import {Authentication} from "../models/authentication.model";

@Injectable({providedIn: 'root'})
export class AuthService {

  cookieService = inject(CookieService);

  constructor(private http: HttpClient, private router:Router) {
  }

  login(username:string, password:string ) {
    let autenticationModel = {username: username, password: password} as Authentication
    this.http.post('/authenticate', autenticationModel).subscribe(value => console.log(value))
  }

  getJwtTokenFromCookie(){
    return this.cookieService.get('JWT')
  }

  getJwtTokenFromLocalStorage(){
    return localStorage.getItem('JWT')
  }

  isAuthenticated(route: ActivatedRouteSnapshot) {
    let jwtTokenFromCookie = this.getJwtTokenFromCookie();
    let jwtTokenFromLocalStorage = this.getJwtTokenFromLocalStorage();

    let cookieInvalid = jwtTokenFromCookie == undefined || jwtTokenFromCookie == '';
    let localStorageInvalid = jwtTokenFromLocalStorage == undefined || jwtTokenFromLocalStorage == '';

    if (cookieInvalid && localStorageInvalid) {
      this.router.navigate(['/login']);
      return false;
    }

    return true;
  }
}
