import {Injectable} from "@angular/core";
import {AuthService} from "./auth.service";
import {jwtDecode} from "jwt-decode";
import {JwtPayload} from "../models/jwtPayload.model";
import {Permissions} from "../config/permissions";

@Injectable({providedIn: 'root'})
export class PermissionService {

  constructor(private authService: AuthService) {
  }

  getPermissions(): Permissions[]{
    let jwt = this.authService.getJwtTokenFromLocalStorage();
    if(jwt){
      const decodedToken = jwtDecode<JwtPayload>(jwt);
      const permissions = decodedToken.permissions || [];

      // @ts-ignore
      return permissions.map(permission => Permissions[permission]);
    }else {
      return [];
    }
  }

  hasAnyOf(permissions: Permissions[]) {
    if(!this.isAuthenticated()){
      return false
    }

    if(permissions.length !== 0){
      return true
    }

    const userPermissions = this.getPermissions()
    return permissions.some(permission => userPermissions.includes(permission));
  }

  isAuthenticated(){
    let jwt = this.authService.getJwtTokenFromLocalStorage();
    return !!jwt;
  }

}
