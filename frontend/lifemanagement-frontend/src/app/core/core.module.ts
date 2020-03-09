import {CommonModule} from '@angular/common';
import {NgModule} from '@angular/core';
import {AuthErrorResponseHandler} from './error-handlers/auth-error-response.handler';
import {DefaultErrorHandler} from './error-handlers/default-error.handler';
import {NoConnectionErrorHandler} from './error-handlers/no-connection-error.handler';
import {ServerSideErrorHandler} from './error-handlers/server-side-error.handler';
import {ValidationErrorHandler} from './error-handlers/validation-error.handler';
import {SecuredDirective} from './security/secured.directive';

@NgModule({
  imports: [
    CommonModule,
  ],
  providers: [
    AuthErrorResponseHandler,
    DefaultErrorHandler,
    NoConnectionErrorHandler,
    ServerSideErrorHandler,
    ValidationErrorHandler,
  ],
  declarations: [
    SecuredDirective
  ],
  exports: [
    SecuredDirective
  ]
})
export class CoreModule { }
