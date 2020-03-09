import {HttpEvent, HttpHandler, HttpInterceptor, HttpRequest, } from '@angular/common/http';
import {Injectable} from '@angular/core';
import {TranslateService} from '@ngx-translate/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AddLanguageHeaderInterceptor implements HttpInterceptor {

  constructor(private translate: TranslateService) { }

  intercept = (req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> => {
    const userLang = this.translate.currentLang;

    if (userLang) {
      // Clone the request to add the new header
      const clonedRequest = req.clone({headers: req.headers.set('Accept-Language', userLang)});

      // Pass the cloned request instead of the original request to the next handle
      return next.handle(clonedRequest);
    } else {
      return next.handle(req);
    }
  }
}
