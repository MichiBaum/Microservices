import {HttpErrorResponse} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {Observable, of} from 'rxjs';
import {ToastMessageService} from '../../toast-message/toast-message.service';
import {HttpErrorResponseHandler} from './http-error-response.handler';
import {LogGenerator} from './log-generator.namespace';

@Injectable()
export class ServerSideErrorHandler implements HttpErrorResponseHandler {

  constructor(private toastMessageService: ToastMessageService) { }

  matches(error: HttpErrorResponse): boolean {
    return error.status === 500;
  }

  handle(error: HttpErrorResponse): Observable<any> {
    const message = LogGenerator.createToastError(error);
    this.toastMessageService.emit([
      message
    ]);
    return of([]);
  }

}
