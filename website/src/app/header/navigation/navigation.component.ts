import { Component, OnInit, inject } from '@angular/core';
import {MenubarModule} from "primeng/menubar";
import {MenuItem} from "primeng/api";
import {TranslateModule} from "@ngx-translate/core";
import {RouterNavigationService} from "../../core/services/router-navigation.service";
import {SidebarModule} from "primeng/sidebar";
import {Button, ButtonDirective} from "primeng/button";
import {MenuModule} from "primeng/menu";
import {LightDarkModeService} from "../../core/services/light-dark-mode.service";
import {Ripple} from "primeng/ripple";
import {
  faBars,
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
import {Drawer} from "primeng/drawer";
import {NgIf} from "@angular/common";

@Component({
  selector: 'app-navigation',
  imports: [
    MenubarModule,
    SidebarModule,
    MenuModule,
    Ripple,
    FaIconComponent,
    ImageModule,
    TranslateModule,
    LanguageSelectComponent,
    Drawer,
    NgIf,
    ButtonDirective,
    Button
  ],
  templateUrl: './navigation.component.html',
  styleUrl: './navigation.component.scss'
})
export class NavigationComponent implements OnInit{
  private readonly routerNavigationService = inject(RouterNavigationService);
  private readonly lightDarkModeService = inject(LightDarkModeService);
  private readonly permissionService = inject(PermissionService);
  private readonly authService = inject(AuthService);

  protected readonly buttonIcon = faBars;

  navItems: MenuItem[] = [];
  sidebarVisible: boolean = false;

  menuStyle = {
    border:{
      color: "var(--p-drawer-background)"
    }
  }

  ngOnInit(): void {
    this.navItems = [...this.getNavItems()];

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
        label: 'navigation.apps',
        items: [
          {
            label: Sides.login.translationKey,
            customIcon: faKey,
            visible: !this.permissionService.isAuthenticated(),
            command: () => {
              this.sidebarVisible = false;
              this.routerNavigationService.login();
            }
          } as MenuItem,
          {
            label: Sides.home.translationKey,
            customIcon: faHouse,
            visible: true,
            command: () => {
              this.sidebarVisible = false;
              this.routerNavigationService.home();
            }
          } as MenuItem,
          {
            label: Sides.fitness.translationKey,
            customIcon: faDumbbell,
            visible: this.permissionService.hasAnyOf([Permissions.FITNESS_SERVICE]),
            command: () => {
              this.sidebarVisible = false;
              this.routerNavigationService.fitness();
            }
          } as MenuItem,
          {
            label: Sides.music.translationKey,
            customIcon: faCompactDisc,
            visible: Sides.music.canActivate(this.permissionService),
            command: () => {
              this.sidebarVisible = false;
              this.routerNavigationService.music();
            }
          } as MenuItem,
          {
            label: Sides.chess.translationKey,
            customIcon: faChess,
            visible: true,
            command: () => {
              this.sidebarVisible = false;
              this.routerNavigationService.chess();
            },
          } as MenuItem
        ]
      },
      {
         label: 'navigation.developer-and-project',
         items: [
          {
            label: Sides.about_me.translationKey,
            customIcon: faUser,
            visible: Sides.about_me.canActivate(this.permissionService), // TODO
            command: () => {
              this.sidebarVisible = false;
              this.routerNavigationService.about_me();
            }
          },
          {
            label: Sides.donate.translationKey,
            customIcon: faCoffee,
            visible: Sides.donate.canActivate(this.permissionService),
            command: () => {
              this.sidebarVisible = false;
              this.routerNavigationService.donate();
            }
          } as MenuItem,
          {
            label: Sides.microservices.translationKey,
            customIcon: faMicrochip,
            visible: Sides.microservices.canActivate(this.permissionService),
            command: () => {
              this.sidebarVisible = false;
              this.routerNavigationService.microservices();
            }
          } as MenuItem,
          {
            label: 'navigation.github',
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
        label: 'navigation.else',
        items: [
          {
            label: 'navigation.light-dark-mode',
            customIcon: faLightbulb,
            visible: true,
            command: () => {
              this.sidebarVisible = false;
              this.lightDarkModeService.changeMode(document);
            }
          } as MenuItem,
          {
            label: Sides.imprint.translationKey,
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
