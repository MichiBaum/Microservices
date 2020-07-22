import {HttpErrorResponse} from '@angular/common/http';
import {Injectable, Injector} from '@angular/core';
import {TranslateService} from '@ngx-translate/core';
import {Message} from 'primeng';
import {Observable, of} from 'rxjs';
import {environment} from '../../../environments/environment';
import {ToastMessageService} from '../../toast-message/toast-message.service';
import {IHttpErrorResponseHandler} from './i-http-error-response.handler';
import {LogGenerator} from './log-generator.namespace';

@Injectable()
export class AuthErrorResponseHandler implements IHttpErrorResponseHandler {

  private translate: TranslateService;

  constructor(
    private toastMessageService: ToastMessageService,
    private injector: Injector
  ) {
  }

  matches(error: HttpErrorResponse): boolean {
    return error.status === 403;
  }

  handle(error: HttpErrorResponse): Observable<any> {
    if (this.translate == null) {
      this.translate = this.injector.get(TranslateService);
    }

    const message = this.generateMessage(error);
    this.toastMessageService.emit([
      message
    ]);

    return of();
  }

  private generateMessage(error: HttpErrorResponse): Message {
    if (environment.show_errors === true) {
      return LogGenerator.createToastError(error);
    } else {
      return LogGenerator.createToastError(
        {
          name: this.translate.instant('error.403.name'),
          message: this.translate.instant('error.403.message')
        } as Error
      );
    }
  }

}
