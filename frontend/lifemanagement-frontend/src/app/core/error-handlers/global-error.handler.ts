import {ErrorHandler, Injectable, Injector} from '@angular/core';
import {AppService} from '../../app.service';
import {LogGenerator} from './log-generator.namespace';

@Injectable()
export class GlobalErrorHandler implements ErrorHandler {

  constructor(private injector: Injector) {
  }

  handleError(error) {
    const appService = this.injector.get(AppService);
    const log = LogGenerator.createLog(error);
    appService.log(log);

    // IMPORTANT: Rethrow the error otherwise it gets swallowed
    throw error;
  }

}
