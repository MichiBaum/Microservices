import {TestBed} from '@angular/core/testing';
import {PermissionService} from './permission.service';
import {AuthService} from './auth.service';
import {Permissions} from '../config/permissions'

describe('PermissionService', () => {
    let service: PermissionService;
    let authServiceSpy: jasmine.SpyObj<AuthService>;

    beforeEach(() => {
        const spy = jasmine.createSpyObj('AuthService', ['getJwtTokenFromLocalStorage']);

        TestBed.configureTestingModule({
            providers: [
                PermissionService,
                {provide: AuthService, useValue: spy}
            ]
        });

        service = TestBed.inject(PermissionService);
        authServiceSpy = TestBed.inject(AuthService) as jasmine.SpyObj<AuthService>;
    });

    it('should return permissions when JWT is present', () => {
        const mockJwt = 'mock.jwt.token';
        authServiceSpy.getJwtTokenFromLocalStorage.and.returnValue(mockJwt);

        const decodedToken = {permissions: ['ADMIN_SERVICE', 'CHESS_SERVICE']};
        // spyOn(globalThis, 'jwtDecode').and.returnValue(decodedToken); // TODO TS2345: Argument of type "jwtDecode" is not assignable to parameter of type

        const permissions = service.getPermissions();

        expect(permissions).toContain(Permissions.ADMIN_SERVICE);
        expect(permissions).toContain(Permissions.CHESS_SERVICE);
    });

    it('should return empty array when JWT is not present', () => {
        authServiceSpy.getJwtTokenFromLocalStorage.and.returnValue(null);

        const permissions = service.getPermissions();

        expect(permissions).toEqual([]);
    });

    it('should return false for `hasAnyOf` when not authenticated', () => {
        spyOn(service, 'isAuthenticated').and.returnValue(false);

        const result = service.hasAnyOf([Permissions.ADMIN_SERVICE]);

        expect(result).toBeFalse();
    });

    it('should return false for `hasAnyOf` when no permissions provided', () => {
        spyOn(service, 'isAuthenticated').and.returnValue(true);

        const result = service.hasAnyOf([]);

        expect(result).toBeFalse();
    });

    it('should return true for `hasAnyOf` when user has one of the permissions', () => {
        spyOn(service, 'isAuthenticated').and.returnValue(true);
        spyOn(service, 'getPermissions').and.returnValue([Permissions.ADMIN_SERVICE, Permissions.CHESS_SERVICE]);

        const result = service.hasAnyOf([Permissions.ADMIN_SERVICE, Permissions.REGISTRY_SERVICE]);

        expect(result).toBeTrue();
    });

    it('should return false for `hasAnyOf` when user does not have any of the permissions', () => {
        spyOn(service, 'isAuthenticated').and.returnValue(true);
        spyOn(service, 'getPermissions').and.returnValue([Permissions.CHESS_SERVICE]);

        const result = service.hasAnyOf([Permissions.ADMIN_SERVICE, Permissions.REGISTRY_SERVICE]);

        expect(result).toBeFalse();
    });

    it('should be authenticated if JWT is present', () => {
        authServiceSpy.getJwtTokenFromLocalStorage.and.returnValue('mock.jwt.token');

        const isAuthenticated = service.isAuthenticated();

        expect(isAuthenticated).toBeTrue();
    });

    it('should not be authenticated if JWT is not present', () => {
        authServiceSpy.getJwtTokenFromLocalStorage.and.returnValue(null);

        const isAuthenticated = service.isAuthenticated();

        expect(isAuthenticated).toBeFalse();
    });
});
