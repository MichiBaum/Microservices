import {inject, Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {Application} from "../models/admin/admin.model";
import {EnvironmentConfig} from "../config/environment.config";

@Injectable({providedIn: 'root'})
export class AdminService {
  private readonly http = inject(HttpClient);
  private readonly environment = inject(EnvironmentConfig);

  applications(): Observable<Application[]> {
    return this.http.get<Application[]>(this.environment.adminService() + '/applications', {headers: {"Accept": "application/json"}})
  }


}
