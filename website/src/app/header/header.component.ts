import {Component, inject, OnDestroy, signal} from '@angular/core';
import {NavigationComponent} from "./navigation/navigation.component";
import {TranslateModule, TranslateService} from "@ngx-translate/core";
import {DropdownModule} from "primeng/dropdown";
import {FormsModule} from "@angular/forms";
import {LanguageConfig} from "../core/config/language.config";
import {HeaderService} from "../core/services/header.service";
import {Title} from "@angular/platform-browser";
import {LogoutComponent} from "../logout/logout.component";
import {MenubarModule} from "primeng/menubar";
import {Subscription} from "rxjs";

@Component({
  selector: 'app-header',
  imports: [
    NavigationComponent,
    TranslateModule,
    DropdownModule,
    FormsModule,
    LogoutComponent,
    MenubarModule
  ],
  templateUrl: './header.component.html',
  styleUrl: './header.component.scss'
})
export class HeaderComponent implements OnDestroy{
  private readonly languageConfig = inject(LanguageConfig);
  private readonly translate = inject(TranslateService);
  private readonly headerService = inject(HeaderService);
  private readonly titleService = inject(Title);

  title = signal<string>("application.title")

  titleChangeSubscription: Subscription =this.headerService.titleChangeEmitter.subscribe(value => {
    this.changeTitle(value)
  });
  languageChangeSubscription: Subscription = this.languageConfig.languageChanged.subscribe(() => {
    this.changeTitle(this.title())
  });

  ngOnDestroy(): void {
    this.titleChangeSubscription?.unsubscribe();
    this.languageChangeSubscription?.unsubscribe();
  }

  /**
   * Updates the title of the application by setting a new title value and updating the browser's title.
   *
   * @param {string} title - The new title to set.
   * @return {void} This method does not return a value.
   */
  changeTitle(title: string): void {
    this.title.set(title)
    this.translate.get(title).subscribe(
      (value: string) => this.titleService.setTitle(value)
    )
  }

}
