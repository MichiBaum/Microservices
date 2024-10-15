import {Injectable} from "@angular/core";
import {AuthService} from "./auth.service";
import {jwtDecode} from "jwt-decode";
import {JwtPayload} from "../models/jwtPayload.model";

@Injectable({providedIn: 'root'})
export class PermissionService {

  constructor(private authService: AuthService) {
  }

  getPermissions(): string[]{
    let jwt = this.authService.getJwtTokenFromLocalStorage();
    if(jwt){
      const decodedToken = jwtDecode<JwtPayload>(jwt);
      return decodedToken.permissions || [];
    }else {
      return [];
    }
  }

  canActivate(param: any) {
    var permissions = this.getPermissions()

  }
}
