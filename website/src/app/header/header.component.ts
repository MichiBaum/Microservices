import { Component, OnInit, inject } from '@angular/core';
import {NavigationComponent} from "./navigation/navigation.component";
import {TranslateModule, TranslateService} from "@ngx-translate/core";
import {DropdownModule} from "primeng/dropdown";
import {FormsModule} from "@angular/forms";
import {LanguageConfig} from "../core/config/language.config";
import {HeaderService} from "../core/services/header.service";
import {Title} from "@angular/platform-browser";
import {LogoutComponent} from "../logout/logout.component";
import {MenubarModule} from "primeng/menubar";

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
export class HeaderComponent implements OnInit{
  private readonly languageConfig = inject(LanguageConfig);
  private readonly translate = inject(TranslateService);
  private readonly headerService = inject(HeaderService);
  private readonly titleService = inject(Title);

  title = "application.title"


  ngOnInit(): void {
    this.headerService.titleChangeEmitter.subscribe(value => {
      this.changeTitle(value)
    })
    this.languageConfig.languageChanged.subscribe(() => {
      this.changeTitle(this.title)
    })
  }

  changeTitle(title: string){
    this.title = title
    this.titleService.setTitle(this.translate.instant(title))
  }


}
