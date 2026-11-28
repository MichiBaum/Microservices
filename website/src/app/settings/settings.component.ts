import {Component, computed} from '@angular/core';
import {NgClass} from '@angular/common';
import {RouterLink, RouterOutlet} from '@angular/router';
import {FaIconComponent} from '@fortawesome/angular-fontawesome';
import {faAngleDown, faGear, faShieldHalved} from '@fortawesome/free-solid-svg-icons';
import {TranslatePipe} from '@ngx-translate/core';
import {MenuItem} from 'primeng/api';
import {Badge} from 'primeng/badge';
import {Divider} from 'primeng/divider';
import {Menubar} from 'primeng/menubar';
import {Tag} from 'primeng/tag';

@Component({
  selector: 'app-settings',
  imports: [
    Badge,
    Divider,
    FaIconComponent,
    Menubar,
    Tag,
    RouterLink,
    RouterOutlet,
    NgClass,
    TranslatePipe
  ],
  templateUrl: './settings.component.html',
  styleUrl: './settings.component.css'
})
export class SettingsComponent {
  protected readonly faGear = faGear;
  protected readonly faAngleDown = faAngleDown;

  protected readonly menuItems = computed<MenuItem[]>(() => [
    {
      label: 'settings.nav.security',
      customIcon: faShieldHalved,
      routerLink: '/settings/security'
    }
  ]);
}
