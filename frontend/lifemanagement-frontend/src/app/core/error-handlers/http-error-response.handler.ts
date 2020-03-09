import {HttpErrorResponse} from '@angular/common/http';
import {Observable} from 'rxjs';

export interface HttpErrorResponseHandler {
  matches(error: HttpErrorResponse): boolean;
  handle(error: HttpErrorResponse): Observable<any>;
}
