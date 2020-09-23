import {HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {AuthService} from '../services/auth.service';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {

  constructor(private authService: AuthService ) {}

  intercept(req: HttpRequest<any>,
            next: HttpHandler): Observable<HttpEvent<any>> {

    const headerName = this.authService.getTokenHeaderName();
    const value = this.authService.getTokenValue();

    if (headerName && value) {
      const cloned = req.clone({
        headers: req.headers.set(headerName, value)
      });

      return next.handle(cloned);
    } else {
      return next.handle(req);
    }
  }
}
