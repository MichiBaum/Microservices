import {Component} from '@angular/core';
import {RouterLink, RouterLinkActive, RouterOutlet} from '@angular/router';
import {FaIconComponent, IconDefinition} from '@fortawesome/angular-fontawesome';
import {faShieldHalved} from '@fortawesome/free-solid-svg-icons';
import {TranslatePipe} from '@ngx-translate/core';

export interface SettingsNavItem {
  label: string;
  routerLink: string;
  icon: IconDefinition;
}

@Component({
  selector: 'app-settings',
  imports: [
    RouterOutlet,
    RouterLink,
    RouterLinkActive,
    FaIconComponent,
    TranslatePipe
  ],
  templateUrl: './settings.component.html',
  styleUrl: './settings.component.css'
})
export class SettingsComponent {
  protected readonly navItems: SettingsNavItem[] = [
    {
      label: 'settings.nav.security',
      routerLink: '/settings/security',
      icon: faShieldHalved
    }
  ];
}
