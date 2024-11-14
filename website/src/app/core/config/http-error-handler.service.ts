import {HttpErrorResponse} from "@angular/common/http";
import {throwError} from "rxjs";
import {Injectable} from "@angular/core";
import {UserInfoService} from "../services/user-info.service";

@Injectable({providedIn: 'root'})
export class HttpErrorHandler {

  handleError(error: HttpErrorResponse, userInfoService: UserInfoService) { // TODO if userInfoService is injected via constructor it is undefined. I have tried providing in app.config
    let errorMessage: string;
    let errorSummary: string;

    switch (error.status) {
      case 0:
        // A client-side or network error occurred. Handle it accordingly.
        console.error('An error occurred:', error.error);
        errorSummary = 'Client Error';
        errorMessage = 'A client-side or network error occurred.';
        break;
      case 400:
        errorSummary = 'Bad Request';
        errorMessage = 'The server cannot or will not process the request due to a client error.';
        break;
      case 401:
        errorSummary = 'Unauthorized';
        errorMessage = 'The request requires user authentication.';
        break;
      case 403:
        errorSummary = 'Forbidden';
        errorMessage = 'The server understood the request, but is refusing to fulfill it.';
        break;
      case 404:
        errorSummary = 'Not Found';
        errorMessage = 'The server has not found anything matching the Request-URI.';
        break;
      case 500:
        errorSummary = 'Internal Server Error';
        errorMessage = 'The server encountered an unexpected condition which prevented it from fulfilling the request.';
        break;
      case 501:
        errorSummary = 'Not Implemented';
        errorMessage = 'The server does not support the functionality required to fulfill the request.';
        break;
      case 503:
        errorSummary = 'Service Unavailable';
        errorMessage = 'The server is currently unavailable. This is generally a temporary state.';
        break;
      default:
        errorSummary = 'Unknown Error';
        errorMessage = 'An unexpected error occurred. Please try again later.';
    }

    userInfoService.error(errorSummary, errorMessage);

    // Return an observable with a user-facing error message.
    return throwError(() => new Error(errorMessage));
  }

}
