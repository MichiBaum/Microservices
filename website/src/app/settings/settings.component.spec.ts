import { describe, it, expect, beforeEach } from 'vitest';
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

  it('should render the settings title and description', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    const title = compiled.querySelector('h1');
    const description = compiled.querySelector('p');

    expect(title?.textContent?.trim()).toBe('Settings');
    expect(description?.textContent?.trim()).toBe('Manage your account settings');
  });

  it('should render navigation with translated aria-label and nav items', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    const nav = compiled.querySelector('nav');
    expect(nav?.getAttribute('aria-label')).toBe('Settings navigation');

    const navLinks = compiled.querySelectorAll('nav a');
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
