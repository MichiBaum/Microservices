import {HttpErrorResponse} from "@angular/common/http";
import {throwError} from "rxjs";
import {Injectable} from "@angular/core";
import {UserInfoService} from "../services/user-info.service";

@Injectable({providedIn: 'root'})
export class HttpErrorHandler {

  handleError(error: HttpErrorResponse, userInfoService: UserInfoService, customErrorMatcher: CustomErrorMatching) { // TODO if userInfoService is injected via constructor it is undefined. I have tried providing in app.config
    let errorSummary: string = "";
    let errorMessage: string = "";

    const customConditionMatches = customErrorMatcher?.matcher(error) ?? false;

    if(customConditionMatches){
      errorSummary = customErrorMatcher?.summary ?? "";
      errorMessage = customErrorMatcher?.details ?? "";
    }

    userInfoService.error(errorSummary, errorMessage);

    // Return an observable with a user-facing error message.
    return throwError(() => new Error(errorSummary + " -> " + errorMessage));
  }

}

export interface CustomErrorMatching {
  matcher: (error: HttpErrorResponse) => boolean,
  summary: string,
  details: string
}
