import {Component, OnInit} from '@angular/core';
import {TranslateService} from '@ngx-translate/core';
import {MenuItem} from 'primeng';
import {environment} from '../../environments/environment';
import {LanguageConfig} from '../core/language.config';
import {PermissionEnum} from '../core/models/enum/permission.enum';
import {AuthService} from '../core/services/auth.service';
import {RouternavigationService} from '../core/services/routernavigation.service';
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
        icon: 'fas fa-house-user',
        command: () => {
          this.sidebarVisible = false;
          this.routernavigationService.homeNavigate();
        }
      } as MenuItem,

      {
        label: this.translate.instant('navigation.usersettings'),
        icon: 'fas fa-users-cog',
        command: () => {
          this.sidebarVisible = false;
          this.routernavigationService.usersettingsNavigate();
        }
      } as MenuItem,

      {
        label: this.translate.instant('navigation.logs'),
        icon: 'fas fa-cogs',
        command: () => {
          this.sidebarVisible = false;
          this.routernavigationService.logmanagementNavigate();
        },
        visible: this.authService.hasAnyPermission([PermissionEnum.SEE_LOGS])
      } as MenuItem,

      {
        label: this.translate.instant('navigation.imprint'),
        icon: 'fas fa-stamp',
        command: () => {
          this.sidebarVisible = false;
          this.routernavigationService.imprintNavigate();
        }
      } as MenuItem,

      {
        label: this.translate.instant('navigation.privacy-policy'),
        icon: 'fas fa-user-shield',
        command: () => {
          this.sidebarVisible = false;
          this.routernavigationService.privacyPolicyNavigate();
        }
      } as MenuItem,

      {
        label: this.translate.instant('navigation.github'),
        icon: 'fab fa-github',
        command: () => {
          this.sidebarVisible = false;
          const url = 'https://github.com/MichiBaum/lifemanagement';
          window.open(url, '_blank');
        },
        visible: this.authService.hasAnyPermission([PermissionEnum.DEVELOP_TOOLS])
      } as MenuItem,

      {
        label: this.translate.instant('navigation.frontend_documentation'),
        icon: 'fab fa-github',
        command: () => {
          this.sidebarVisible = false;
          this.routernavigationService.frontendDocumentationNavigate();
        },
        visible: this.authService.hasAnyPermission([PermissionEnum.DEVELOP_TOOLS])
      } as MenuItem,

      {
        label: this.translate.instant('navigation.backend_api'),
        icon: 'fas fa-server',
        command: () => {
          this.sidebarVisible = false;
          const url = environment.api_url + '/swagger-ui.html';
          window.open(url, '_blank');
        },
        visible: this.authService.hasAnyPermission([PermissionEnum.DEVELOP_TOOLS])
      } as MenuItem,

      {
        label: this.translate.instant('navigation.logout'),
        icon: 'fas fa-sign-out-alt',
        command: () => {
          this.authService.logout();
          this.sidebarVisible = false;
        }
      } as MenuItem

    ] as MenuItem[];
  }

}
