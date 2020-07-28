import {HttpErrorResponse} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {ForbiddenErrorResponseHandler} from './forbidden-error-response.handler';
import {IHttpErrorResponseHandler} from './i-http-error-response.handler';
import {MethodNotAllowedErrorHandler} from './method-not-allowed-error.handler';
import {NoConnectionErrorHandler} from './no-connection-error.handler';
import {NotFoundErrorHandler} from './not-found-error.handler';
import {ServerSideErrorHandler} from './server-side-error.handler';
import {ValidationErrorHandler} from './validation-error.handler';
import {UnauthorizedErrorResponseHandler} from './unauthorized-error-response.handler';

@Injectable()
export class DefaultErrorHandler implements IHttpErrorResponseHandler {

  handlers: IHttpErrorResponseHandler[];

  constructor(
    private validationErrorHandler: ValidationErrorHandler,
    private authErrorHandler: ForbiddenErrorResponseHandler,
    private unauthorizedErrorHandler: UnauthorizedErrorResponseHandler,
    private serverSideErrorHandler: ServerSideErrorHandler,
    private notFoundErrorHandler: NotFoundErrorHandler,
    private methodNotAllowedErrorHandler: MethodNotAllowedErrorHandler,
    private noConnectionErrorHandler: NoConnectionErrorHandler
  ) {
    this.handlers = [
      validationErrorHandler,
      authErrorHandler,
      unauthorizedErrorHandler,
      serverSideErrorHandler,
      notFoundErrorHandler,
      methodNotAllowedErrorHandler,
      noConnectionErrorHandler
    ];
  }

  handle(error: HttpErrorResponse): Observable<any> {
    for (const handler of this.handlers) {
      if (handler.matches(error)) {
        return handler.handle(error);
      }
    }
  }

  matches(error: HttpErrorResponse): boolean {
    return true;
  }

}
