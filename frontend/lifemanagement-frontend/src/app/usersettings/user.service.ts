import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {ExportUser, User} from '../core/models/user.model';
import {ApiService} from '../core/services/api.service';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  constructor(private apiService: ApiService) {
  }

  getAll = (): Observable<User[]> => {
    return this.apiService.getAll('/users', false);
  }

  getMe = (): Observable<User> => {
    return this.apiService.getAll('/users/me', false);
  }

  save(user: ExportUser): Observable<User> {
    return this.apiService.postAll('/users/' + user.id, user);
  }

}
