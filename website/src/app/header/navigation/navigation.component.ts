import {Component, OnInit} from '@angular/core';
import {MenubarModule} from "primeng/menubar";
import {MenuItem} from "primeng/api";
import {TranslateModule, TranslateService} from "@ngx-translate/core";
import {RouterNavigationService} from "../../core/services/router-navigation.service";
import {LanguageConfig} from "../../core/config/language.config";
import {SidebarModule} from "primeng/sidebar";
import {SlideMenuModule} from "primeng/slidemenu";
import {ButtonDirective} from "primeng/button";
import {MenuModule} from "primeng/menu";
import {LightDarkModeService} from "../../core/services/light-dark-mode.service";
import {Ripple} from "primeng/ripple";
import {
  faChess,
  faCoffee,
  faCompactDisc,
  faDumbbell,
  faHouse,
  faKey,
  faLightbulb,
  faMicrochip,
  faStamp,
  faUser
} from "@fortawesome/free-solid-svg-icons";
import {FaIconComponent} from "@fortawesome/angular-fontawesome";
import {faGithub} from "@fortawesome/free-brands-svg-icons";
import {Sides} from "../../core/config/sides";
import {PermissionService} from "../../core/services/permission.service";
import {AuthService} from "../../core/services/auth.service";
import {ImageModule} from "primeng/image";
import {Permissions} from "../../core/config/permissions";
import {LanguageSelectComponent} from "../../language-select/language-select.component";

@Component({
  selector: 'app-navigation',
  standalone: true,
  imports: [
    MenubarModule,
    SidebarModule,
    SlideMenuModule,
    ButtonDirective,
    MenuModule,
    Ripple,
    FaIconComponent,
    ImageModule,
    TranslateModule,
    LanguageSelectComponent
  ],
  templateUrl: './navigation.component.html',
  styleUrl: './navigation.component.scss'
})
export class NavigationComponent implements OnInit{
  navItems: MenuItem[] = [];
  sidebarVisible: boolean = false;

  constructor(
    private readonly translate: TranslateService,
    private readonly languageConfig: LanguageConfig,
    private readonly routerNavigationService: RouterNavigationService,
    private readonly lightDarkModeService: LightDarkModeService,
    private readonly permissionService: PermissionService,
    private readonly authService: AuthService
  ) {

  }

  ngOnInit(): void {
    this.navItems = [...this.getNavItems()];

    this.languageConfig.languageChanged.subscribe(() => {
      this.navItems = [...this.getNavItems()];
    });

    this.authService.successLoginEmitter.subscribe(() => {
      this.navItems = [...this.getNavItems()];
    });

    this.authService.logoutEmitter.subscribe(() => {
      this.navItems = [...this.getNavItems()];
    })

  }

  getNavItems = () =>
     [
      {
        label: this.translate.instant('navigation.apps'),
        items: [
          {
            label: this.translate.instant(Sides.login.translationKey),
            customIcon: faKey,
            visible: !this.permissionService.isAuthenticated(),
            command: () => {
              this.sidebarVisible = false;
              this.routerNavigationService.login();
            }
          } as MenuItem,
          {
            label: this.translate.instant(Sides.home.translationKey),
            customIcon: faHouse,
            command: () => {
              this.sidebarVisible = false;
              this.routerNavigationService.home();
            }
          } as MenuItem,
          {
            label: this.translate.instant(Sides.fitness.translationKey),
            customIcon: faDumbbell,
            visible: this.permissionService.hasAnyOf([Permissions.FITNESS_SERVICE]),
            command: () => {
              this.sidebarVisible = false;
              this.routerNavigationService.fitness();
            }
          } as MenuItem,
          {
            label: this.translate.instant(Sides.music.translationKey),
            customIcon: faCompactDisc,
            visible: Sides.music.canActivate(this.permissionService),
            command: () => {
              this.sidebarVisible = false;
              this.routerNavigationService.music();
            }
          } as MenuItem,
          {
            label: this.translate.instant(Sides.chess.translationKey),
            customIcon: faChess,
            command: () => {
              this.sidebarVisible = false;
              this.routerNavigationService.chess();
            },
          } as MenuItem
        ]
      },
      {
         label: this.translate.instant('navigation.developer-and-project'),
         items: [
          {
            label: this.translate.instant(Sides.about_me.translationKey),
            customIcon: faUser,
            visible: Sides.about_me.canActivate(this.permissionService), // TODO
            command: () => {
              this.sidebarVisible = false;
              this.routerNavigationService.about_me();
            }
          },
          {
            label: this.translate.instant(Sides.donate.translationKey),
            customIcon: faCoffee,
            visible: Sides.donate.canActivate(this.permissionService),
            command: () => {
              this.sidebarVisible = false;
              this.routerNavigationService.donate();
            }
          } as MenuItem,
          {
            label: this.translate.instant(Sides.microservices.translationKey),
            customIcon: faMicrochip,
            visible: Sides.microservices.canActivate(this.permissionService),
            command: () => {
              this.sidebarVisible = false;
              this.routerNavigationService.microservices();
            }
          } as MenuItem,
          {
            label: this.translate.instant('navigation.github'),
            customIcon: faGithub,
            visible: true,
            command: () => {
              this.sidebarVisible = false;
              this.routerNavigationService.github();
            }
          } as MenuItem
         ]
       },
      {
        label: this.translate.instant('navigation.else'),
        items: [
          {
            label: this.translate.instant('navigation.light-dark-mode'),
            customIcon: faLightbulb,
            visible: true,
            command: () => {
              this.sidebarVisible = false;
              this.lightDarkModeService.changeMode(document);
            }
          } as MenuItem,
          {
            label: this.translate.instant(Sides.imprint.translationKey),
            customIcon: faStamp,
            visible: Sides.imprint.canActivate(this.permissionService),
            command: () => {
              this.sidebarVisible = false;
              this.routerNavigationService.imprint();
            }
          } as MenuItem,
        ]
      }
    ] as MenuItem[];


}
