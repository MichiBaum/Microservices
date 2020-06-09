import {HttpClient, HttpHeaders, HttpParams} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {TranslateService} from '@ngx-translate/core';
import {Message} from 'primeng';
import {Observable, of} from 'rxjs';
import {catchError, tap} from 'rxjs/operators';
import {environment} from '../../../environments/environment';
import {ToastMessageService} from '../../toast-message/toast-message.service';
import {DefaultErrorHandler} from '../error-handlers/default-error.handler';
import {HttpErrorResponseHandler} from '../error-handlers/http-error-response.handler';
import {ToastMessageSeverity} from '../models/enum/toast-message-severity.enum';

const contentTypeJson = {
  headers: new HttpHeaders({'Content-Type': 'application/json'})
};

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  constructor(
    private http: HttpClient,
    private defaultErrorHandler: DefaultErrorHandler,
    private toastMessageService: ToastMessageService,
    // private translate: TranslateService // TODO Error: Cannot instantiate cyclic dependency! TranslateService
  ) { }

  getAll = (
    path: string,
    cacheable = true,
    params: any = new HttpParams(),
    errorHandler: HttpErrorResponseHandler = this.defaultErrorHandler
  ): Observable<any> => {
    return this.http.get(`${environment.api_url}${path}`, {params}).pipe(
      tap ( x => {
        if (cacheable) {
          localStorage.setItem(JSON.stringify({path, params}), JSON.stringify(x));
        }
      }),
      catchError((error) => {
        if (cacheable) {
          const localStorageValue = localStorage.getItem(JSON.stringify({path, params}));
          if (localStorageValue) {
            this.emitToastCachedData();
            return of(JSON.parse(localStorageValue));
          }
        }
        errorHandler.handle(error);
        return of([]);
      })
    );
  }

  postAll = (
    path: string,
    body: any = new HttpParams(),
    errorHandler: HttpErrorResponseHandler = this.defaultErrorHandler
  ): Observable<any> => {
    return this.http.post(`${environment.api_url}${path}`, body, contentTypeJson).pipe(
      catchError((error) => errorHandler.handle(error))
    );
  }

  private emitToastCachedData = () => {
    this.toastMessageService.emit([
      {
        severity: ToastMessageSeverity.WARNING,
        // tslint:disable-next-line:max-line-length TODO remove
        summary: 'error.cached.summary', // this.translate.instant('error.cached.summary') TODO Error: Cannot instantiate cyclic dependency! TranslateService
        // tslint:disable-next-line:max-line-length TODO remove
        detail: 'error.cached.detail', // this.translate.instant('error.cached.detail') TODO Error: Cannot instantiate cyclic dependency! TranslateService
        life: 1000
      } as Message
    ]);
  }

}
