import {MissingTranslationHandlerParams} from '@ngx-translate/core';
import {Message} from 'primeng';
import {LogLevel} from '../models/enum/logLevel.enum';
import {ToastMessageSeverity} from '../models/enum/toast-message-severity.enum';
import {ExportLog} from '../models/log.model';

export namespace LogGenerator {

  export function createLog(
    error: Error
  ): ExportLog {
    return {
      loggerName: 'angular',
      date: Date.now(),
      callerClass: 'GlobalErrorHandler',
      callerFilename: 'GlobalErrorHandler',
      callerLine: '',
      callerMethod: 'handleError',
      formattedMessage: error.message ? error.message : error.toString(),
      level: LogLevel.ERROR
    } as ExportLog;
  }

  export function createTranslationLog(
    params: MissingTranslationHandlerParams,
    language: string
  ): ExportLog {
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
    } as ExportLog;
  }

  export function createToastError(
    error: Error,
    severity: ToastMessageSeverity = ToastMessageSeverity.ERROR,
    summary: string = error.name,
    detail: string = error.message,
    closable: boolean = true
  ): Message {
    return {severity, closable, summary, detail} as Message;
  }

}

