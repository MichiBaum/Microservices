import { describe, it, expect, beforeEach, vi } from 'vitest';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { By } from '@angular/platform-browser';
import { RouterOutlet } from '@angular/router';
import { provideTranslateService, TranslateService } from '@ngx-translate/core';
import { SettingsComponent } from './settings.component';

describe('SettingsComponent', () => {
  let component: SettingsComponent;
  let fixture: ComponentFixture<SettingsComponent>;

  beforeEach(async () => {
    Object.defineProperty(window, 'matchMedia', {
      writable: true,
      value: vi.fn().mockImplementation((query: string) => ({
        matches: false,
        media: query,
        onchange: null,
        addListener: vi.fn(),
        removeListener: vi.fn(),
        addEventListener: vi.fn(),
        removeEventListener: vi.fn(),
        dispatchEvent: vi.fn()
      }))
    });

    await TestBed.configureTestingModule({
      imports: [SettingsComponent],
      providers: [
        provideRouter([]),
        provideTranslateService({
          fallbackLang: 'en',
          lang: 'en'
        })
      ]
    }).compileComponents();

    const translate = TestBed.inject(TranslateService);
    translate.setTranslation('en', {
      settings: {
        title: 'Settings',
        'meta-description': 'Manage your account settings',
        nav: {
          'aria-label': 'Settings navigation',
          security: 'Security'
        }
      }
    });
    translate.use('en');

    fixture = TestBed.createComponent(SettingsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should render PrimeNG menubar with start icon and security navigation item', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    const menubar = compiled.querySelector('p-menubar');
    expect(menubar).toBeTruthy();

    const navLinks = compiled.querySelectorAll('a.p-menubar-item-link');
    expect(navLinks.length).toBe(1);

    const securityLink = navLinks[0];
    expect(securityLink.getAttribute('href')).toBe('/settings/security');
    expect(securityLink.textContent?.trim()).toContain('Security');
  });

  it('should render a router-outlet for child routes', () => {
    const outlet = fixture.debugElement.query(By.directive(RouterOutlet));
    expect(outlet).toBeTruthy();
  });
});
