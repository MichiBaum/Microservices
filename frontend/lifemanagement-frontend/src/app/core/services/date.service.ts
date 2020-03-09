import {Injectable} from '@angular/core';
import {DateFormat} from '../models/enum/date-format.enum';
import {LocalStorageService} from './local-storage.service';

@Injectable({
  providedIn: 'root'
})
export class DateService {

  localStorageKey = 'dateFormat';

  constructor(private localStorageService: LocalStorageService) { }

  setFormat = (dateFormat: DateFormat) => {
    this.localStorageService.setKeyAndValue(this.localStorageKey, dateFormat);
  }

  getFormat = (): string | undefined => {
    return this.localStorageService.getValue(this.localStorageKey);
  }

}
