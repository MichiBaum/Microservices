import {HttpEvent, HttpHandlerFn, HttpRequest} from "@angular/common/http";
import {Observable} from "rxjs";
import {AUTH_JWT_KEY} from "../api-services/auth.service";

export function authJwtInterceptor(req: HttpRequest<unknown>, next: HttpHandlerFn): Observable<HttpEvent<unknown>> {
    const jwt = localStorage.getItem(AUTH_JWT_KEY);

    if (!jwt) {
        return next(req);
    }

    const newRequest = req.clone({
        headers: req.headers.set('Authorization', `Bearer ${jwt}`)
    });

    return next(newRequest);
}
