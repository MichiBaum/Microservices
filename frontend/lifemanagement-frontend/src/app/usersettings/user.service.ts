import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {IPermission} from '../core/models/permission.model';
import {IUpdateUser, IUser} from '../core/models/user.model';
import {ApiService} from '../core/services/api.service';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  constructor(private apiService: ApiService) {
  }

  getAll = (): Observable<IUser[]> => {
    return this.apiService.getAll('/users', false);
  }

  getAllPermissions = (): Observable<IPermission[]> => {
    return this.apiService.getAll('/permissions', false);
  }

  getMe = (): Observable<IUser> => {
    return this.apiService.getAll('/users/me', false);
  }

  save(user: IUpdateUser): Observable<IUser> {
    return this.apiService.postAll('/users/' + user.id, user);
  }

  savePermissions(userId: number, permissionIds: number[]): Observable<any> {
    return this.apiService.postAll('/users/' + userId + '/permissions', permissionIds);
  }
}
