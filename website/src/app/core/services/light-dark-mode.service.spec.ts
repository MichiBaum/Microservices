import {TestBed} from '@angular/core/testing';
import {LightDarkMode, LightDarkModeService} from './light-dark-mode.service';

describe('LightDarkModeService', () => {
    let service: LightDarkModeService;
    let documentStub: Document;

    beforeEach(() => {
        TestBed.configureTestingModule({});
        service = TestBed.inject(LightDarkModeService);
        documentStub = document.implementation.createHTMLDocument();
        const linkElement = documentStub.createElement('link');
        linkElement.id = 'app-theme';
        linkElement.href = 'theme-light.css';
        documentStub.head.appendChild(linkElement);
    });

    it('should create the service', () => {
        expect(service).toBeTruthy();
    });

    it('should return the current mode from localStorage', () => {
        localStorage.setItem('theme', 'dark');
        expect(service.currentMode()).toBe(LightDarkMode.dark);
        localStorage.setItem('theme', 'light');
        expect(service.currentMode()).toBe(LightDarkMode.light);
        localStorage.removeItem('theme');
    });

    it('should return system dark mode if theme is not set in localStorage', () => {
        spyOn(window, 'matchMedia').and.returnValue({matches: true} as MediaQueryList);
        expect(service.currentMode()).toBe(LightDarkMode.dark);
    });

    it('should change mode to dark if current mode is light', () => {
        service.changeMode(documentStub);
        const linkElement = documentStub.getElementById('app-theme') as HTMLLinkElement;
        expect(linkElement.href).toContain('theme-dark.css');
        expect(localStorage.getItem('theme')).toBe('dark');
    });

    it('should change mode to light if current mode is dark', () => {
        const linkElement = documentStub.getElementById('app-theme') as HTMLLinkElement;
        linkElement.href = 'theme-dark.css';
        service.changeMode(documentStub);
        expect(linkElement.href).toContain('theme-light.css');
        expect(localStorage.getItem('theme')).toBe('light');
    });

    it('should change to specified mode when changeModeTo is called', () => {
        service.changeModeTo(documentStub, LightDarkMode.dark);
        expect(documentStub.getElementById('app-theme')?.getAttribute('href')).toContain('theme-dark.css');
        expect(localStorage.getItem('theme')).toBe('dark');

        service.changeModeTo(documentStub, LightDarkMode.light);
        expect(documentStub.getElementById('app-theme')?.getAttribute('href')).toContain('theme-light.css');
        expect(localStorage.getItem('theme')).toBe('light');
    });

    it('should initialize the theme based on current mode', () => {
        spyOn(service, 'currentMode').and.returnValue(LightDarkMode.dark);
        service.init(documentStub);
        expect(documentStub.getElementById('app-theme')?.getAttribute('href')).toContain('theme-dark.css');
    });
});
