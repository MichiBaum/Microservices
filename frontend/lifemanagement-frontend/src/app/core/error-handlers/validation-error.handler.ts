import {HttpErrorResponse} from '@angular/common/http';
import {Injectable, Injector} from '@angular/core';
import {TranslateService} from '@ngx-translate/core';
import {Message} from 'primeng';
import {Observable, of} from 'rxjs';
import {environment} from '../../../environments/environment';
import {ToastMessageService} from '../../toast-message/toast-message.service';
import {IHttpErrorResponseHandler} from './i-http-error-response.handler';
import {LogGenerator} from './log-generator.namespace';
import {IValidationError} from "../models/validation-error.model";

@Injectable()
export class ValidationErrorHandler implements IHttpErrorResponseHandler {

  private translate: TranslateService;

  constructor(
    private toastMessageService: ToastMessageService,
    private injector: Injector
  ) {
  }

  matches(error: HttpErrorResponse): boolean {
    return error.status === 400;
  }

  handle(error: HttpErrorResponse): Observable<any> {
    if (this.translate == null) {
      this.translate = this.injector.get(TranslateService);
    }

    console.log(error);
    this.generateToastMessage(error);
    return of([]);
  }

  private generateToastMessage(error: HttpErrorResponse) {
    const validationError = error as IValidationError;
    validationError.name = this.translate.instant('error.400.name');
    validationError.error.validationErrors.map(value => this.translate.instant(value));
    validationError.error.validationErrors.forEach(
      (value, index) => {
        const message = LogGenerator.createValidationToastError(validationError, index);
        this.toastMessageService.emit([
          message
        ]);
      }
    );
  }
}
