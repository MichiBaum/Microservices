import {inject, Injectable} from "@angular/core";
import {AuthService} from "./auth.service";
import {jwtDecode} from "jwt-decode";
import {JwtPayload} from "../models/jwtPayload.model";
import {Permissions} from "../config/permissions";

@Injectable({providedIn: 'root'})
export class PermissionService {
  private authService = inject(AuthService);


  getJwt(): JwtPayload | undefined {
    let jwt = this.authService.getJwtTokenFromLocalStorage();
    if(jwt) {
      return jwtDecode<JwtPayload>(jwt);
    }
    return undefined
  }

  getPermissions(jwt: JwtPayload): Permissions[]{
      const permissions = jwt.permissions || [];
      // @ts-ignore
      return permissions.map(permission => Permissions[permission]);
  }

  hasAnyOf(permissions: Permissions[]) {
    if(permissions.length == 0){
      throw new Error("Permissions list is empty")
    }

    const jwt = this.getJwt()
    if(jwt == undefined){
      return false;
    }

    if(!this.isValid(jwt)){
      return false
    }

    const userPermissions = this.getPermissions(jwt)
    return permissions.some(permission => userPermissions.includes(permission));
  }

  isAuthenticated(){
    let jwt = this.getJwt();
    if(jwt == undefined){
      return false;
    }
    return this.isValid(jwt);
  }

  isValid(jwt: JwtPayload) { // TODO implement time zones
    let now = Date.now();

    const exp = jwt.exp
    const expiration = exp * 1000;
    let isExpired = now >= expiration;

    const iat = jwt.iat
    const issued = exp * 1000;
    let isIssuedBeforeNow = now <= issued;

    return !isExpired && isIssuedBeforeNow;
  }

}
