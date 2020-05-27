import {HttpClient, HttpHeaders, HttpParams} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {Observable, of} from 'rxjs';
import {catchError} from 'rxjs/operators';
import {environment} from '../../../environments/environment';
import {DefaultErrorHandler} from '../error-handlers/default-error.handler';
import {HttpErrorResponseHandler} from '../error-handlers/http-error-response.handler';

const contentTypeJson = {
  headers: new HttpHeaders({'Content-Type': 'application/json'})
};

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  constructor(
    private http: HttpClient,
    private defaultErrorHandler: DefaultErrorHandler
  ) { }

  getAll = (path: string, params: any = new HttpParams()): Observable<any> => {
    return this.http.get(`${environment.api_url}${path}`, {params}).pipe(
      catchError(() => {
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
}
