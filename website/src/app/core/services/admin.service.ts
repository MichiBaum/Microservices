import {inject, Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {catchError, Observable} from "rxjs";
import {HttpErrorHandler} from "../config/http-error-handler.service";
import {UserInfoService} from "./user-info.service";
import {Application} from "../models/admin/admin.model";
import {EnvironmentConfig} from "../config/environment.config";

@Injectable({providedIn: 'root'})
export class AdminService {
  private readonly http = inject(HttpClient);
  private readonly environment = inject(EnvironmentConfig);
  private readonly httpErrorConfig = inject(HttpErrorHandler);
  private readonly userInfoService = inject(UserInfoService);

  applications(): Observable<Application[]> {
    return this.http.get<Application[]>(this.environment.adminService() + '/applications', {headers: {"Accept": "application/json"}})
      .pipe(catchError(err => this.httpErrorConfig.handleError(err, this.userInfoService)));
  }


}
