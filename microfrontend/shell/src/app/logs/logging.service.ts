import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {ILogFilter} from '../core/models/log-filter.model';
import {ILog} from '../core/models/log.model';
import {ApiService} from '../core/services/api.service';

@Injectable({
  providedIn: 'root'
})
export class LoggingService {
  constructor(private apiService: ApiService) { }

  getLogs = (filter: ILogFilter): Observable<ILog[]> => {
    return this.apiService.getAll('/logs', true, filter);
  }

  getAllLevels = () => {
    return this.apiService.getAll('/logs/levels');
  }

  setSeen = (id: number, seen: boolean): Observable<ILog> => {
    return this.apiService.postAll('/logs/' + id + '/seen', seen);
  }
}
