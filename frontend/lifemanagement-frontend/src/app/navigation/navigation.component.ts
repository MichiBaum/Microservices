import { Component, OnInit } from '@angular/core';
import {TranslateService} from '@ngx-translate/core';
import {MenuItem} from 'primeng';
import {LanguageConfig} from '../core/language.config';
import {Permission} from '../core/security/permission.enum';
import {AuthService} from '../core/services/auth.service';
import {LoginService} from '../login/login.service';

@Component({
  selector: 'app-navigation',
  templateUrl: './navigation.component.html',
  styleUrls: ['./navigation.component.scss'],
})
export class NavigationComponent implements OnInit {

  navItems: MenuItem[];
  sidebarVisible: boolean;

  constructor(
    private translate: TranslateService,
    public authService: AuthService,
    private languageConfig: LanguageConfig,
    private loginService: LoginService
  ) {

    this.languageConfig.languageChanged.subscribe(() => {
      this.setItems();
    });

    this.loginService.loginEmitter.subscribe(() => {
      this.setItems();
    });

  }

  ngOnInit(): void {
    this.setItems();
  }

  setItems = (): void => {
    this.navItems = [
      {
        label: this.translate.instant('navigation.home'),
        icon: 'pi pi-home',
        routerLink: 'home',
        command: () => {
          this.sidebarVisible = false;
        }
      } as MenuItem,
      {
        label: this.translate.instant('navigation.check-list'),
        icon: 'pi pi-list',
        routerLink: 'check-list',
        command: () => {
          this.sidebarVisible = false;
        }
      } as MenuItem,
      {
        label: this.translate.instant('navigation.usersettings'),
        icon: 'pi pi-user-edit',
        routerLink: 'usersettings',
        command: () => {
          this.sidebarVisible = false;
        }
      } as MenuItem,
      {
        label: this.translate.instant('navigation.logs'),
        icon: 'pi pi-info',
        routerLink: 'logmanagement',
        command: () => {
          this.sidebarVisible = false;
        },
        visible: this.authService.hasAnyPermission([Permission.SEE_LOGS])
      } as MenuItem,
      {
        label: this.translate.instant('navigation.logout'),
        icon: 'pi pi-power-off',
        command: () => {
          this.authService.logout();
          this.sidebarVisible = false;
        }
      } as MenuItem
    ] as MenuItem[];
  }

}
