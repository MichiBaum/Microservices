import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {Permission} from '../core/models/permission.model';
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

  getAllPermissions = (): Observable<Permission[]> => {
    return this.apiService.getAll('/permissions', false);
  }

  getMe = (): Observable<User> => {
    return this.apiService.getAll('/users/me', false);
  }

  save(user: ExportUser): Observable<User> {
    return this.apiService.postAll('/users/' + user.id, user);
  }

  savePermissions(user: ExportUser): Observable<any> {
    return this.apiService.postAll('/users/' + user.id + '/permissions', user.permissions);
  }
}
