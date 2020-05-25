import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {CheckListItem} from '../core/models/check-list-item.model';
import {ApiService} from '../core/services/api.service';

@Injectable({
  providedIn: 'root'
})
export class CheckListService {
  constructor(private apiService: ApiService) { }

  getCheckListItems = (): Observable<CheckListItem[]> => {
    return this.apiService.getAll('/checkListItems');
  }

}
