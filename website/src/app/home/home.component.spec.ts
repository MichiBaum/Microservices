import {TestBed} from '@angular/core/testing';
import {HomeComponent} from './home.component';
import {Subject} from 'rxjs';
import {HeaderService} from "../core/services/header.service";
import {RouterNavigationService} from "../core/services/router-navigation.service";
import {PermissionService} from "../core/services/permission.service";
import {AuthService} from "../core/services/auth.service";
import {Sides} from "../core/config/sides";
import {TranslateModule, TranslateService} from "@ngx-translate/core";

describe('HomeComponent', () => {
  let component: HomeComponent;
  let headerService: HeaderService;
  let routerNavigationService: RouterNavigationService;
  let permissionService: PermissionService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HomeComponent,
        TranslateModule.forRoot()
      ],
      providers: [
        {provide: HeaderService, useValue: {changeTitle: jasmine.createSpy(), titleChangeEmitter: new Subject<string>()}},
        {provide: RouterNavigationService, useValue: {navigate: jasmine.createSpy()}},
        {provide: PermissionService, useValue: {isAuthenticated: jasmine.createSpy(), getPermissions: jasmine.createSpy()}},
        {provide: AuthService, useValue: {getJwtTokenFromLocalStorage: jasmine.createSpy()}}
      ]
    }).compileComponents();

    const fixture = TestBed.createComponent(HomeComponent);
    component = fixture.componentInstance;
    headerService = TestBed.inject(HeaderService);
    routerNavigationService = TestBed.inject(RouterNavigationService);
    permissionService = TestBed.inject(PermissionService);
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should call changeTitle on HeaderService with correct translation key', () => {
    expect(headerService.changeTitle).toHaveBeenCalledWith(Sides.home.translationKey);
  });

  it('should return true if canActivateChess returns true', () => {
    spyOn(Sides.chess, 'canActivate').and.returnValue(true);
    expect(component.canActivateChess()).toBe(true);
  });

  it('should return false if canActivateChess returns false', () => {
    spyOn(Sides.chess, 'canActivate').and.returnValue(false);
    expect(component.canActivateChess()).toBe(false);
  });

  it('should return true if canLogin returns true', () => {
    spyOn(Sides.login, 'canActivate').and.returnValue(true);
    expect(component.canLogin()).toBe(true);
  });

  it('should return false if canLogin returns false', () => {
    spyOn(Sides.login, 'canActivate').and.returnValue(false);
    expect(component.canLogin()).toBe(false);
  });

  it('should return true if canActivateFitness returns true', () => {
    spyOn(Sides.fitness, 'canActivate').and.returnValue(true);
    expect(component.canActivateFitness()).toBe(true);
  });

  it('should return false if canActivateFitness returns false', () => {
    spyOn(Sides.fitness, 'canActivate').and.returnValue(false);
    expect(component.canActivateFitness()).toBe(false);
  });

  it('should return true if canActivateMusic returns true', () => {
    spyOn(Sides.music, 'canActivate').and.returnValue(true);
    expect(component.canActivateMusic()).toBe(true);
  });

  it('should return false if canActivateMusic returns false', () => {
    spyOn(Sides.music, 'canActivate').and.returnValue(false);
    expect(component.canActivateMusic()).toBe(false);
  });
});
