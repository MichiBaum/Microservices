import {Injectable} from "@angular/core";
import {HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from "@angular/common/http";
import {Observable} from "rxjs";
import {AuthService} from "../services/auth.service";

@Injectable()
export class AuthInterceptor implements HttpInterceptor {

  constructor(private authService: AuthService) {
  }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    if(!this.authService.jwtIsPresent()){
      return next.handle(req);
    }

    req = req.clone({
      setHeaders: {
        // 'Content-Type' : 'application/json; charset=utf-8',
        // 'Accept'       : 'application/json',
        'Authorization': `Bearer ${this.authService.getJwtTokenFromLocalStorage()}`,
      },
    });

    return next.handle(req);
  }
}
