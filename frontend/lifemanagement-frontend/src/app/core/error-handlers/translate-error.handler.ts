import {Injectable} from '@angular/core';
import {MissingTranslationHandler, MissingTranslationHandlerParams} from '@ngx-translate/core';
import {AppService} from '../../app.service';
import {defaultLanguage} from '../language.config';
import {IExportLog} from '../models/log.model';
import {LogGenerator} from './log-generator.namespace';

@Injectable()
export class TranslateErrorHandler implements MissingTranslationHandler {

  constructor(private appService: AppService) { }

  handle(params: MissingTranslationHandlerParams) {
    const language = localStorage.getItem('languageIso') || defaultLanguage.isoCode;
    const log: IExportLog = LogGenerator.createTranslationLog(params, language);

    this.appService.log(log);
    return params.key;
  }
}
