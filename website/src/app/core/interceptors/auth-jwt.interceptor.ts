import { Injectable, inject } from "@angular/core";
import {HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from "@angular/common/http";
import {Observable} from "rxjs";
import {AuthService} from "../services/auth.service";

/**
 * AuthInterceptor class that implements the HttpInterceptor.
 * This class intercepts outgoing HTTP requests and adds an Authorization header
 * if a JWT token is present.
 */
@Injectable()
export class AuthJwtInterceptor implements HttpInterceptor {
  private authService = inject(AuthService);

  /**
   * Intercepts HTTP requests to add an Authorization header if a JWT token is present.
   *
   * @param {HttpRequest<any>} request - The outgoing HTTP request.
   * @param {HttpHandler} next - The next interceptor in the chain.
   * @return {Observable<HttpEvent<any>>} An Observable of the HTTP event.
   */
  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    if(!this.authService.jwtIsPresent()){
      return next.handle(request);
    }

    request = request.clone({
      setHeaders: {
        // 'Content-Type' : 'application/json; charset=utf-8',
        // 'Accept'       : 'application/json',
        'Authorization': `Bearer ${this.authService.getJwtTokenFromLocalStorage()}`,
      },
    });

    return next.handle(request);
  }
}
