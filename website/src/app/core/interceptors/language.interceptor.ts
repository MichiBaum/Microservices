import {HttpEvent, HttpHandlerFn, HttpRequest} from "@angular/common/http";
import {Observable} from "rxjs";
import {inject} from "@angular/core";
import {LanguageConfig} from "../config/language.config";


export function languageInterceptor(request: HttpRequest<unknown>, next: HttpHandlerFn): Observable<HttpEvent<unknown>> {
    const languageConfig = inject(LanguageConfig);

    let languageCode = languageConfig.current?.isoCode ?? languageConfig.defaultLanguage.isoCode;

    const newRequest = request.clone({
        headers: request.headers.set('Accept-Language', languageCode),
    });

    return next(newRequest);

}
