import {inject, Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {catchError, Observable} from "rxjs";
import {environment} from "../../../environments/environment";
import {HttpErrorHandler} from "../config/http-error-handler.service";
import {UserInfoService} from "./user-info.service";
import {Application} from "../models/admin/admin.model";

@Injectable({providedIn: 'root'})
export class AdminService {
  private readonly http = inject(HttpClient);
  private readonly httpErrorConfig = inject(HttpErrorHandler);
  private readonly userInfoService = inject(UserInfoService);

  applications(): Observable<Application[]> {
    return this.http.get<Application[]>(environment.adminService + '/applications', {headers: {"Accept": "application/json"}})
      .pipe(catchError(err => this.httpErrorConfig.handleError(err, this.userInfoService)));
  }


}
