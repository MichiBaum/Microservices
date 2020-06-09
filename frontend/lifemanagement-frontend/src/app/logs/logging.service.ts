import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {LogFilter} from '../core/models/log-filter.model';
import {Log} from '../core/models/log.model';
import {ApiService} from '../core/services/api.service';

@Injectable({
  providedIn: 'root'
})
export class LoggingService {
  constructor(private apiService: ApiService) { }

  getLogs = (filter: LogFilter): Observable<Log[]> => {
    return this.apiService.getAll('/logs', true, filter);
  }

  getAllLevels = () => {
    return this.apiService.getAll('/logs/levels');
  }

  setSeen = (id: number, seen: boolean): Observable<Log> => {
    return this.apiService.postAll('/logs/' + id + '/seen', seen);
  }
}
