import {MissingTranslationHandlerParams} from '@ngx-translate/core';
import {Message} from 'primeng';
import {LogLevel} from '../models/enum/logLevel.enum';
import {ToastMessageSeverity} from '../models/enum/toast-message-severity.enum';
import {IExportLog} from '../models/log.model';

export namespace LogGenerator {

  export function createLog(
    error: Error
  ): IExportLog {
    return {
      loggerName: 'angular',
      date: Date.now(),
      callerClass: 'GlobalErrorHandler',
      callerFilename: 'GlobalErrorHandler',
      callerLine: '',
      callerMethod: 'handleError',
      formattedMessage: error.message ? error.message : error.toString(),
      level: LogLevel.ERROR
    } as IExportLog;
  }

  export function createTranslationLog(
    params: MissingTranslationHandlerParams,
    language: string
  ): IExportLog {
    return {
      loggerName: 'angular',
      date: Date.now(),
      arg0: params.key,
      callerClass: 'TranslateErrorHandler',
      callerFilename: 'TranslateErrorHandler',
      callerLine: '',
      callerMethod: 'handle',
      formattedMessage: 'Missing text for: ' + params.key + ' with language ' + language,
      level: LogLevel.ERROR
    } as IExportLog;
  }

  export function createToastError(
    error: Error,
    severity: ToastMessageSeverity = ToastMessageSeverity.ERROR,
    summary: string = error.name,
    detail: string = error.message,
    closable: boolean = true,
    life: number = 10000
  ): Message {
    return {severity, closable, summary, detail, life} as Message;
  }

}
