import {Injectable} from '@angular/core';
import {IExportLog} from './core/models/log.model';
import {ApiService} from './core/services/api.service';

@Injectable({
  providedIn: 'root'
})
export class AppService {
  constructor(private apiService: ApiService) { }

  log = (log: IExportLog) => {
    this.apiService.postAll('/logs', log)
      .toPromise()
      .then(() => {console.log('sending log to backend success'); })
      .catch(() => {console.log('sending log to backend failed'); });
  }

}
