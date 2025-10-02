import {inject} from "@angular/core";
import {HttpEvent, HttpHandlerFn, HttpRequest} from "@angular/common/http";
import {Observable} from "rxjs";
import {AuthService} from "../api-services/auth.service";

export function authJwtInterceptor(req: HttpRequest<unknown>, next: HttpHandlerFn): Observable<HttpEvent<unknown>> {
    const authService = inject(AuthService);

    if(!authService.jwtIsPresent()){
        return next(req);
    }

    const newRequest = req.clone({
        headers: req.headers.set('Authorization', `Bearer ${authService.getJwtTokenFromLocalStorage()}`)
    });

    return next(newRequest);
}
