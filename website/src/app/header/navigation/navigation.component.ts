import {Component, OnInit} from '@angular/core';
import {MenubarModule} from "primeng/menubar";
import {MenuItem} from "primeng/api";
import {TranslateService} from "@ngx-translate/core";
import {RouterNavigationService} from "../../core/services/router-navigation.service";
import {LanguageConfig} from "../../core/config/language.config";
import {SidebarModule} from "primeng/sidebar";
import {SlideMenuModule} from "primeng/slidemenu";
import {ButtonDirective} from "primeng/button";
import {MenuModule} from "primeng/menu";
import {LightDarkModeService} from "../../core/services/light-dark-mode.service";
import {Ripple} from "primeng/ripple";
import {faChess, faCoffee, faHouse, faLightbulb, faMicrochip, faStamp, faUser} from "@fortawesome/free-solid-svg-icons";
import {FaIconComponent} from "@fortawesome/angular-fontawesome";
import {faGithub} from "@fortawesome/free-brands-svg-icons";
import {Sides} from "../../core/config/sides";
import {PermissionService} from "../../core/services/permission.service";
import {AuthService} from "../../core/services/auth.service";
import {ImageModule} from "primeng/image";

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
  ],
  templateUrl: './navigation.component.html',
  styleUrl: './navigation.component.scss'
})
export class NavigationComponent implements OnInit{
  navItems: MenuItem[] = [];
  sidebarVisible: boolean = false;

  constructor(
    private translate: TranslateService,
    private languageConfig: LanguageConfig,
    private routerNavigationService: RouterNavigationService,
    private lightDarkModeService: LightDarkModeService,
    private permissionService: PermissionService,
    private authService: AuthService
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
            customIcon: faHouse,
            visible: Sides.login.canActivate(this.permissionService),
            command: () => {
              this.sidebarVisible = false;
              this.routerNavigationService.login();
            }
          } as MenuItem,
          {
            label: this.translate.instant(Sides.home.translationKey),
            customIcon: faHouse,
            visible: Sides.home.canActivate(this.permissionService),
            command: () => {
              this.sidebarVisible = false;
              this.routerNavigationService.home();
            }
          } as MenuItem,
          {
            label: this.translate.instant(Sides.chess.translationKey),
            customIcon: faChess,
            visible: Sides.chess.canActivate(this.permissionService), // TODO
            command: () => {
              this.sidebarVisible = false;
              this.routerNavigationService.chess();
            }
          } as MenuItem
          ]
      },
      {
        label: this.translate.instant('navigation.settings-and-else'),// TODO
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
            label: this.translate.instant(Sides.microservices.translationKey),
            customIcon: faMicrochip,
            visible: Sides.microservices.canActivate(this.permissionService), // TODO
            command: () => {
              this.sidebarVisible = false;
              this.routerNavigationService.microservices();
            }
          } as MenuItem,
          {
            label: this.translate.instant('navigation.light-dark-mode'),
            customIcon: faLightbulb,
            visible: true, // TODO
            command: () => {
              this.sidebarVisible = false;
              this.lightDarkModeService.changeMode(document);
            }
          } as MenuItem,
          {
            label: this.translate.instant(Sides.imprint.translationKey),
            customIcon: faStamp,
            visible: Sides.imprint.canActivate(this.permissionService), // TODO
            command: () => {
              this.sidebarVisible = false;
              this.routerNavigationService.imprint();
            }
          } as MenuItem,
          {
            label: this.translate.instant('navigation.github'),
            customIcon: faGithub,
            visible: true, // TODO
            command: () => {
              this.sidebarVisible = false;
              this.routerNavigationService.github();
            }
          } as MenuItem,
          {
            label: this.translate.instant('navigation.buymeacoffee'),
            customIcon: faCoffee,
            visible: true, // TODO
            command: () => {
              this.sidebarVisible = false;
              this.routerNavigationService.donate();
            }
          } as MenuItem
        ]
      }
    ] as MenuItem[];


}
