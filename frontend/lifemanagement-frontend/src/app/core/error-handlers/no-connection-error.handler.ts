import {HttpErrorResponse} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {Observable, of} from 'rxjs';
import {ToastMessageService} from '../../toast-message/toast-message.service';
import {HttpErrorResponseHandler} from './http-error-response.handler';
import {LogGenerator} from './log-generator.namespace';

@Injectable()
export class NoConnectionErrorHandler implements HttpErrorResponseHandler {

  constructor(
    private toastMessageService: ToastMessageService
  ) { }

  matches(error: HttpErrorResponse): boolean {
    return true;
  }

  handle(error: HttpErrorResponse): Observable<any> {
    console.log(error);
    const message = LogGenerator.createToastError(error);
    this.toastMessageService.emit([
      message
    ]);
    return of([]);
  }

}
