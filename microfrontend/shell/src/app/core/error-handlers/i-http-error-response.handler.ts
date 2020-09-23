import {HttpErrorResponse} from '@angular/common/http';
import {Observable} from 'rxjs';

export interface IHttpErrorResponseHandler {
  matches(error: HttpErrorResponse): boolean;
  handle(error: HttpErrorResponse): Observable<any>;
}
