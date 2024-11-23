import {ComponentFixture, TestBed} from '@angular/core/testing';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {TranslateModule} from '@ngx-translate/core';
import {LoginComponent} from './login.component';
import {of} from 'rxjs';
import {AuthService} from "../core/services/auth.service";
import {HeaderService} from "../core/services/header.service";
import {RouterNavigationService} from "../core/services/router-navigation.service";
import {Sides} from "../core/config/sides";

describe('LoginComponent', () => {
    let component: LoginComponent;
    let fixture: ComponentFixture<LoginComponent>;
    let authService: AuthService;
    let headerService: HeaderService;
    let routerService: RouterNavigationService;

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            declarations: [LoginComponent],
            imports: [
                ReactiveFormsModule,
                FormsModule,
                HttpClientTestingModule,
                TranslateModule.forRoot()
            ],
            providers: [
                AuthService,
                HeaderService,
                RouterNavigationService
            ]
        }).compileComponents();

        fixture = TestBed.createComponent(LoginComponent);
        component = fixture.componentInstance;
        authService = TestBed.inject(AuthService);
        headerService = TestBed.inject(HeaderService);
        routerService = TestBed.inject(RouterNavigationService);
    });

    // Test ngOnInit
    it('should call changeTitle on headerService when ngOnInit is called', () => {
        const changeTitleSpy = spyOn(headerService, 'changeTitle');
        component.ngOnInit();
        expect(changeTitleSpy).toHaveBeenCalledWith(Sides.login.translationKey);
    });

    // Test login method
    it('should not call authService.login if username or password is empty', () => {
        const loginSpy = spyOn(authService, 'login');

        component.username = '';
        component.password = '';
        component.login();
        expect(loginSpy).not.toHaveBeenCalled();

        component.username = 'user';
        component.password = '';
        component.login();
        expect(loginSpy).not.toHaveBeenCalled();

        component.username = '';
        component.password = 'pass';
        component.login();
        expect(loginSpy).not.toHaveBeenCalled();
    });

    it('should call authService.login if username and password are not empty', () => {
        const loginSpy = spyOn(authService, 'login');
        component.username = 'user';
        component.password = 'pass';
        component.login();
        expect(loginSpy).toHaveBeenCalledWith('user', 'pass');
    });

    // Test register method
    it('should call routerService.register when register is called', () => {
        const registerSpy = spyOn(routerService, 'register').and.callFake(() => {
            of();
        });
        component.register();
        expect(registerSpy).toHaveBeenCalled();
    });

});
