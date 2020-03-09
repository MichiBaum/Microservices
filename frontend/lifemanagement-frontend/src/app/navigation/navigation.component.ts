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
        items: [
          {
            label: this.translate.instant('navigation.home'),
            icon: 'pi pi-home',
            routerLink: '/',
          }
        ]
      },
      {
        label: this.translate.instant('navigation.usersettings'),
        items: [
          {
            label: this.translate.instant('navigation.usersettings'),
            icon: 'pi pi-user-edit',
            routerLink: 'usersettings',
          }
        ]
      },
      {
        label: this.translate.instant('navigation.logs'),
        visible: this.authService.hasAnyPermission([Permission.SEE_LOGS]),
        items: [
          {
            label: this.translate.instant('navigation.logs'),
            icon: 'pi pi-info',
            routerLink: 'logmanagement',
            visible: this.authService.hasAnyPermission([Permission.SEE_LOGS])
          }
        ]
      },
      {
        label: this.translate.instant('navigation.logout'),
        items: [
          {
            label: this.translate.instant('navigation.logout'),
            icon: 'pi pi-power-off',
            command: () => {
              this.authService.logout();
            }
          }
        ]
      }
    ];
  }

}
