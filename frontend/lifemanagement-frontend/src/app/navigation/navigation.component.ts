import {Component, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {TranslateService} from '@ngx-translate/core';
import {MenuItem} from 'primeng';
import {LanguageConfig} from '../core/language.config';
import {PermissionEnum} from '../core/models/enum/permission.enum';
import {AuthService} from '../core/services/auth.service';
import {LoginService} from '../login/login.service';
import {RouternavigationService} from "../core/services/routernavigation.service";

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
    private loginService: LoginService,
    private routernavigationService: RouternavigationService
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
        command: () => {
          this.sidebarVisible = false;
          this.routernavigationService.homeNavigate();
        }
      } as MenuItem,
      {
        label: this.translate.instant('navigation.usersettings'),
        icon: 'pi pi-user-edit',
        command: () => {
          this.sidebarVisible = false;
          this.routernavigationService.logmanagementNavigate();
        }
      } as MenuItem,
      {
        label: this.translate.instant('navigation.logs'),
        icon: 'pi pi-info',
        command: () => {
          this.sidebarVisible = false;
          this.routernavigationService.logmanagementNavigate();
        },
        visible: this.authService.hasAnyPermission([PermissionEnum.SEE_LOGS, PermissionEnum.ADMIN])
      } as MenuItem,
      {
        label: this.translate.instant('navigation.imprint'),
        icon: 'pi pi-info',
        command: () => {
          this.sidebarVisible = false;
          this.routernavigationService.imprintNavigate();
        }
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
