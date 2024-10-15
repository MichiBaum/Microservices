import {Component, OnInit} from '@angular/core';
import {MenubarModule} from "primeng/menubar";
import {MenuItem, PrimeIcons} from "primeng/api";
import {TranslateService} from "@ngx-translate/core";
import {RouternavigationService} from "../../core/services/router-navigation.service";
import {LanguageConfig} from "../../core/config/language.config";
import {SidebarModule} from "primeng/sidebar";
import {SlideMenuModule} from "primeng/slidemenu";
import {ButtonDirective} from "primeng/button";
import {MenuModule} from "primeng/menu";
import {LightDarkModeService} from "../../core/services/light-dark-mode.service";
import {Ripple} from "primeng/ripple";
import {faChess, faCoffee, faHouse, faLightbulb, faMicrochip, faStamp} from "@fortawesome/free-solid-svg-icons";
import {FaIconComponent} from "@fortawesome/angular-fontawesome";
import {faGithub} from "@fortawesome/free-brands-svg-icons";

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
  ],
  templateUrl: './navigation.component.html',
  styleUrl: './navigation.component.scss'
})
export class NavigationComponent implements OnInit{
  navItems: MenuItem[] | undefined;
  sidebarVisible: boolean = false;

  constructor(
    private translate: TranslateService,
    private languageConfig: LanguageConfig,
    private routernavigationService: RouternavigationService,
    private lightDarkModeService: LightDarkModeService
  ) {

    this.languageConfig.languageChanged.subscribe(() => {
      this.setItems();
    });

  }

  ngOnInit(): void {
    this.setItems();
  }

  setItems = (): void => {
    this.navItems = [
      {
        label: 'Apps',
        items: [
          {
            label: this.translate.instant('navigation.home'),
            customIcon: faHouse,
            visible: true,
            command: () => {
              this.sidebarVisible = false;
              this.routernavigationService.home();
            }
          } as MenuItem,
          {
            label: this.translate.instant('navigation.chess'),
            customIcon: faChess,
            visible: true, // TODO
            command: () => {
              this.sidebarVisible = false;
              this.routernavigationService.chess();
            }
          } as MenuItem
          ]
      },
      {
        label: 'Settings & else',// TODO
        items: [
          {
            label: 'Microservices',
            customIcon: faMicrochip,
            visible: true, // TODO
            command: () => {
              this.sidebarVisible = false;
              this.routernavigationService.microservices();
            }
          } as MenuItem,
          {
            label: 'Change light/dark mode',
            customIcon: faLightbulb,
            visible: true, // TODO
            command: () => {
              this.sidebarVisible = false;
              this.lightDarkModeService.changeMode(document);
            }
          } as MenuItem,
          {
            label: this.translate.instant('navigation.imprint'),
            customIcon: faStamp,
            visible: true, // TODO
            command: () => {
              this.sidebarVisible = false;
              this.routernavigationService.imprint();
            }
          } as MenuItem,
          {
            label: this.translate.instant('navigation.github'),
            customIcon: faGithub,
            visible: true, // TODO
            command: () => {
              this.sidebarVisible = false;
              this.routernavigationService.github();
            }
          } as MenuItem,
          {
            label: this.translate.instant('navigation.buymeacoffee'),
            customIcon: faCoffee,
            visible: true, // TODO
            command: () => {
              this.sidebarVisible = false;
              this.routernavigationService.open("https://www.buymeacoffee.com/michibaum");
            }
          } as MenuItem
        ]
      }
    ] as MenuItem[];
  }
}
