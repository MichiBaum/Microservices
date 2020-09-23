import {DatePipe} from '@angular/common';
import {Pipe, PipeTransform} from '@angular/core';
import {LanguageConfig} from '../language.config';
import {DateService} from '../services/date.service';

@Pipe({
  name: 'customDate',
  pure: false
})
export class CustomDatePipe extends DatePipe implements PipeTransform {

  constructor(private dateService: DateService, private languageConfig: LanguageConfig) {
    super(languageConfig.current.isoCode);
  }

  transform(value: any, ...args: any[]): any {
    return super.transform(value, this.dateService.getFormat(), '', this.languageConfig.current.isoCode);
  }

}
