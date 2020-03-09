import {HttpErrorResponse} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {ToastMessageService} from '../../toast-message/toast-message.service';
import {HttpErrorResponseHandler} from './http-error-response.handler';
import {LogGenerator} from './log-generator.namespace';

@Injectable()
export class ValidationErrorHandler implements HttpErrorResponseHandler {

  constructor(private toastMessageService: ToastMessageService) { }

  matches(error: HttpErrorResponse): boolean {
    return error.status === 400;
  }

  handle(error: HttpErrorResponse): Observable<any> {
    const message = LogGenerator.createToastError(error);
    this.toastMessageService.emit([
      message
    ]);
    throw error;
  }

}
