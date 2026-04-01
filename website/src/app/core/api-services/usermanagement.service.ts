import {inject, Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {EnvironmentConfig} from "../config/environment.config";
import {Observable} from "rxjs";
import {User} from "../models/usermanagement/usermanagement.model";

@Injectable({providedIn: 'root'})
export class UserManagementService {
  private http = inject(HttpClient);
  private readonly environment = inject(EnvironmentConfig);

  me(): Observable<User> {
    return this.http.get<User>(this.environment.usermanagementService() + '/users/me')
  }

  egtAll(): Observable<User[]> {
    return this.http.get<User[]>(this.environment.usermanagementService() + '/users')
  }

}
