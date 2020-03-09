import {Injectable, Injector} from '@angular/core';
import {AppService} from '../../app.service';
import {LogLevel} from '../models/enum/logLevel.enum';
import {ExportLog} from '../models/log.model';

@Injectable({
  providedIn: 'root'
})
export class LocalStorageService {

  constructor(private injector: Injector) {
  }

  setKeyAndValue = (key: string, value: string) => {
    try {
      localStorage.setItem(key, value);
    } catch (e) {
      this.logException(e, 'setKeyAndValue');
    }
  }

  getValue = (localStorageKey: string): string | undefined => {
    try {
      return localStorage.getItem(localStorageKey);
    } catch (e) {
      this.logException(e, 'getValue');
    }
    return undefined;
  }

  removeItem = (key: string) => {
    try {
      localStorage.removeItem(key);
    } catch (e) {
      this.logException(e, 'removeItem');
    }
  }

  private logException = (exception, method: string) => {
    const appService = this.injector.get(AppService);
    const log: ExportLog = {
      loggerName: 'angular',
      date: Date.now(),
      callerClass: 'LocalStorageService',
      callerFilename: 'LocalStorageService',
      callerLine: '0',
      callerMethod: method,
      formattedMessage: exception.message ? exception.message : exception.toString(),
      level: LogLevel.WARN
    };

    appService.log(log);
  }
}
