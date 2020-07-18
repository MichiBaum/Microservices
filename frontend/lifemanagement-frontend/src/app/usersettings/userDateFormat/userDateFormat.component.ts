import {Component, OnInit} from '@angular/core';
import {DateFormat} from '../../core/models/enum/date-format.enum';
import {IPrimeNgBase} from '../../core/models/primeng-base.model';
import {DateService} from '../../core/services/date.service';

@Component({
  selector: 'app-user-date-format',
  templateUrl: './userDateFormat.component.html',
  styleUrls: ['./userDateFormat.component.scss']
})
export class UserDateFormatComponent implements OnInit {

  dateFormats: IPrimeNgBase[];
  selectedDateFormat: IPrimeNgBase;

  constructor(private dateService: DateService) {
  }

  ngOnInit(): void {
    this.dateFormats = this.initDateFormats();
    this.selectedDateFormat = this.initSelectedDateFormat();
  }

  private initDateFormats = (): IPrimeNgBase[] => {
    const dateFormats: IPrimeNgBase[] = [];
    for (const dateFormat of Object.keys(DateFormat)) {
      dateFormats.push(
        {
          field: dateFormat,
          label: 'dateFormat.' + dateFormat,
          value: dateFormat
        } as IPrimeNgBase);
    }
    return dateFormats;
  }

  changeDateFormat = (event: any) => {
    this.dateService.setFormat(event.value as DateFormat);
  }

  private initSelectedDateFormat = () => {
    const localStorageDateFormat = this.dateService.getFormat();
    if (localStorageDateFormat) {
      return this.selectedDateFormat = {
        field: localStorageDateFormat,
        label: 'dateFormat.' + localStorageDateFormat,
        value: localStorageDateFormat
      } as IPrimeNgBase;
    }
  }

}
