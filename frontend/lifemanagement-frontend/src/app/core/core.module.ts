import {CommonModule} from '@angular/common';
import {NgModule} from '@angular/core';
import {DefaultErrorHandler} from './error-handlers/default-error.handler';
import {ForbiddenErrorResponseHandler} from './error-handlers/forbidden-error-response.handler';
import {MethodNotAllowedErrorHandler} from './error-handlers/method-not-allowed-error.handler';
import {NoConnectionErrorHandler} from './error-handlers/no-connection-error.handler';
import {NotFoundErrorHandler} from './error-handlers/not-found-error.handler';
import {ServerSideErrorHandler} from './error-handlers/server-side-error.handler';
import {UnauthorizedErrorResponseHandler} from './error-handlers/unauthorized-error-response.handler';
import {ValidationErrorHandler} from './error-handlers/validation-error.handler';
import {SecuredDirective} from './security/secured.directive';

@NgModule({
  imports: [
    CommonModule,
  ],
  providers: [
    ForbiddenErrorResponseHandler,
    UnauthorizedErrorResponseHandler,
    DefaultErrorHandler,
    NoConnectionErrorHandler,
    ServerSideErrorHandler,
    ValidationErrorHandler,
    NotFoundErrorHandler,
    MethodNotAllowedErrorHandler
  ],
  declarations: [
    SecuredDirective
  ],
  exports: [
    SecuredDirective
  ]
})
export class CoreModule { }
