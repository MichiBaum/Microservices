import {HttpErrorResponse} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {AuthErrorResponseHandler} from './auth-error-response.handler';
import {HttpErrorResponseHandler} from './http-error-response.handler';
import {NoConnectionErrorHandler} from './no-connection-error.handler';
import {ServerSideErrorHandler} from './server-side-error.handler';
import {ValidationErrorHandler} from './validation-error.handler';

@Injectable()
export class DefaultErrorHandler implements HttpErrorResponseHandler {

  handlers: HttpErrorResponseHandler[];

  constructor(
    private validationErrorHandler: ValidationErrorHandler,
    private authErrorHandler: AuthErrorResponseHandler,
    private serverSideErrorHandler: ServerSideErrorHandler,
    private noConnectionErrorHandler: NoConnectionErrorHandler
  ) {
    this.handlers = [
      validationErrorHandler,
      authErrorHandler,
      serverSideErrorHandler,
      noConnectionErrorHandler
    ];
  }

  handle(error: HttpErrorResponse): Observable<any> {
    for (const handler of this.handlers) {
      if (handler.matches(error)) {
        return handler.handle(error);
      }
    }
    throw error;
  }

  matches(error: HttpErrorResponse): boolean {
    return true;
  }

}
