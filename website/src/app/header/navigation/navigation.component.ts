import {Component, OnInit} from '@angular/core';
import {MenubarModule} from "primeng/menubar";
import {MenuItem} from "primeng/api";
import {TranslateService} from "@ngx-translate/core";
import {RouternavigationService} from "../../core/services/router-navigation.service";
import {LanguageConfig} from "../../core/config/language.config";
import {SidebarModule} from "primeng/sidebar";
import {SlideMenuModule} from "primeng/slidemenu";
import {ButtonDirective} from "primeng/button";
import {MenuModule} from "primeng/menu";
import {LightDarkModeComponent} from "../../light-dark-mode/light-dark-mode.component";

@Component({
  selector: 'app-navigation',
  standalone: true,
  imports: [
    MenubarModule,
    SidebarModule,
    SlideMenuModule,
    ButtonDirective,
    MenuModule,
    LightDarkModeComponent
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
    private routernavigationService: RouternavigationService
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
        label: this.translate.instant('navigation.home'),
        icon: 'fa fa-home',
        command: () => {
          this.sidebarVisible = false;
          this.routernavigationService.homeNavigate();
        }
      } as MenuItem,
      {
        label: this.translate.instant('navigation.chess'),
        icon: 'fa fa-home',
        command: () => {
          this.sidebarVisible = false;
          this.routernavigationService.chessNavigation();
        }
      } as MenuItem,
      {
        label: this.translate.instant('navigation.imprint'),
        icon: 'fa fa-copyright',
        command: () => {
          this.sidebarVisible = false;
          this.routernavigationService.imprintNavigate();
        }
      } as MenuItem,
      {
        label: this.translate.instant('navigation.github'),
        icon: 'fab fa-github',
        command: () => {
          this.sidebarVisible = false;
          this.routernavigationService.githubNavigate();
        }
      } as MenuItem
    ] as MenuItem[];
  }
}
