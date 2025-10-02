import {inject} from "@angular/core";
import {
    HttpEvent,
    HttpEventType,
    HttpHandlerFn,
    HttpRequest
} from "@angular/common/http";
import {Observable, tap} from "rxjs";
import {UserInfoService} from "../services/user-info.service";

export function httpErrorInterceptor(req: HttpRequest<unknown>, next: HttpHandlerFn): Observable<HttpEvent<unknown>> {

    return next(req).pipe(tap(event => {
        const userInfoService = inject(UserInfoService);

        if (event.type !== HttpEventType.Response) {
            return event;
        }
        if (event.ok) {
            return event;
        }

        let errorSummary: string;
        let errorMessage: string;

        switch (event.status) {
            case 0:
                // A client-side or network error occurred. Handle it accordingly.
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

        return event;
    }))

}
