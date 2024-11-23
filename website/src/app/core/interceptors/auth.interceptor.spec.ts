import {TestBed} from '@angular/core/testing';
import {HttpClientTestingModule, HttpTestingController} from '@angular/common/http/testing';
import {HTTP_INTERCEPTORS, HttpClient} from '@angular/common/http';
import {AuthInterceptor} from './auth.interceptor';
import {AuthService} from "../services/auth.service";

describe('AuthInterceptor', () => {
    let httpMock: HttpTestingController;
    let httpClient: HttpClient;
    let authService: jasmine.SpyObj<AuthService>;

    beforeEach(() => {
        const authServiceSpy = jasmine.createSpyObj('AuthService', ['jwtIsPresent', 'getJwtTokenFromLocalStorage']);

        TestBed.configureTestingModule({
            imports: [HttpClientTestingModule],
            providers: [
                {provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true},
                {provide: AuthService, useValue: authServiceSpy}
            ]
        });

        httpMock = TestBed.inject(HttpTestingController);
        httpClient = TestBed.inject(HttpClient);
        authService = TestBed.inject(AuthService) as jasmine.SpyObj<AuthService>;
    });

    afterEach(() => {
        httpMock.verify();
    });

    it('should add an Authorization header if the JWT is present', () => {
        authService.jwtIsPresent.and.returnValue(true);
        authService.getJwtTokenFromLocalStorage.and.returnValue('test-token');

        httpClient.get('/test').subscribe(response => {
        });

        const httpRequest = httpMock.expectOne('/test');
        expect(httpRequest.request.headers.has('Authorization')).toBeTruthy();
        expect(httpRequest.request.headers.get('Authorization')).toBe('Bearer test-token');
    });

    it('should not add an Authorization header if the JWT is not present', () => {
        authService.jwtIsPresent.and.returnValue(false);

        httpClient.get('/test').subscribe(response => {
        });

        const httpRequest = httpMock.expectOne('/test');
        expect(httpRequest.request.headers.has('Authorization')).toBeFalsy();
    });
});
