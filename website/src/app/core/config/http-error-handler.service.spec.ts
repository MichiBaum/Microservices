import {TestBed} from '@angular/core/testing';
import {HttpErrorHandler} from './http-error-handler.service';
import {HttpErrorResponse} from '@angular/common/http';
import {UserInfoService} from "../services/user-info.service";

// Mock UserInfoService
class MockUserInfoService {
    emitter = jasmine.createSpyObj('Subject', ['next']);

    error(summary: string, details: string) {
        this.emitter.next({severity: 'error', summary, detail: details});
    }
}

describe('HttpErrorHandler', () => {
    let service: HttpErrorHandler;
    let userInfoService: UserInfoService;

    beforeEach(() => {
        TestBed.configureTestingModule({
            providers: [
                HttpErrorHandler,
                {provide: UserInfoService, useClass: MockUserInfoService}
            ]
        });

        service = TestBed.inject(HttpErrorHandler);
        userInfoService = TestBed.inject(UserInfoService);
    });

    it('should handle client-side error', () => {
        const errorResponse = new HttpErrorResponse({
            error: 'Client-side error',
            status: 0,
            statusText: 'Unknown Error'
        });

        service.handleError(errorResponse, userInfoService).subscribe({
            error: (message) => {
                expect(message).toEqual(new Error('A client-side or network error occurred.'));
                expect(userInfoService.messageEmitter.next).toHaveBeenCalledWith({
                    severity: 'error',
                    summary: 'Client Error',
                    detail: 'A client-side or network error occurred.'
                });
            }
        });
    });

    it('should handle 400 error', () => {
        const errorResponse = new HttpErrorResponse({status: 400});

        service.handleError(errorResponse, userInfoService).subscribe({
            error: (message) => {
                expect(message).toEqual(new Error('The server cannot or will not process the request due to a client error.'));
                expect(userInfoService.messageEmitter.next).toHaveBeenCalledWith({
                    severity: 'error',
                    summary: 'Bad Request',
                    detail: 'The server cannot or will not process the request due to a client error.'
                });
            }
        });
    });

    it('should handle 401 error', () => {
        const errorResponse = new HttpErrorResponse({status: 401});

        service.handleError(errorResponse, userInfoService).subscribe({
            error: (message) => {
                expect(message).toEqual(new Error('The request requires user authentication.'));
                expect(userInfoService.messageEmitter.next).toHaveBeenCalledWith({
                    severity: 'error',
                    summary: 'Unauthorized',
                    detail: 'The request requires user authentication.'
                });
            }
        });
    });

    it('should handle 403 error', () => {
        const errorResponse = new HttpErrorResponse({status: 403});

        service.handleError(errorResponse, userInfoService).subscribe({
            error: (message) => {
                expect(message).toEqual(new Error('The server understood the request, but is refusing to fulfill it.'));
                expect(userInfoService.messageEmitter.next).toHaveBeenCalledWith({
                    severity: 'error',
                    summary: 'Forbidden',
                    detail: 'The server understood the request, but is refusing to fulfill it.'
                });
            }
        });
    });

    it('should handle 404 error', () => {
        const errorResponse = new HttpErrorResponse({status: 404});

        service.handleError(errorResponse, userInfoService).subscribe({
            error: (message) => {
                expect(message).toEqual(new Error('The server has not found anything matching the Request-URI.'));
                expect(userInfoService.messageEmitter.next).toHaveBeenCalledWith({
                    severity: 'error',
                    summary: 'Not Found',
                    detail: 'The server has not found anything matching the Request-URI.'
                });
            }
        });
    });

    it('should handle 500 error', () => {
        const errorResponse = new HttpErrorResponse({status: 500});

        service.handleError(errorResponse, userInfoService).subscribe({
            error: (message) => {
                expect(message).toEqual(new Error('The server encountered an unexpected condition which prevented it from fulfilling the request.'));
                expect(userInfoService.messageEmitter.next).toHaveBeenCalledWith({
                    severity: 'error',
                    summary: 'Internal Server Error',
                    detail: 'The server encountered an unexpected condition which prevented it from fulfilling the request.'
                });
            }
        });
    });

    it('should handle 501 error', () => {
        const errorResponse = new HttpErrorResponse({status: 501});

        service.handleError(errorResponse, userInfoService).subscribe({
            error: (message) => {
                expect(message).toEqual(new Error('The server does not support the functionality required to fulfill the request.'));
                expect(userInfoService.messageEmitter.next).toHaveBeenCalledWith({
                    severity: 'error',
                    summary: 'Not Implemented',
                    detail: 'The server does not support the functionality required to fulfill the request.'
                });
            }
        });
    });

    it('should handle 503 error', () => {
        const errorResponse = new HttpErrorResponse({status: 503});

        service.handleError(errorResponse, userInfoService).subscribe({
            error: (message) => {
                expect(message).toEqual(new Error('The server is currently unavailable. This is generally a temporary state.'));
                expect(userInfoService.messageEmitter.next).toHaveBeenCalledWith({
                    severity: 'error',
                    summary: 'Service Unavailable',
                    detail: 'The server is currently unavailable. This is generally a temporary state.'
                });
            }
        });
    });

    it('should handle unknown error', () => {
        const errorResponse = new HttpErrorResponse({status: 999});

        service.handleError(errorResponse, userInfoService).subscribe({
            error: (message) => {
                expect(message).toEqual(new Error('An unexpected error occurred. Please try again later.'));
                expect(userInfoService.messageEmitter.next).toHaveBeenCalledWith({
                    severity: 'error',
                    summary: 'Unknown Error',
                    detail: 'An unexpected error occurred. Please try again later.'
                });
            }
        });
    });
});
