import {Component, OnInit} from '@angular/core';
import {NavigationComponent} from "./navigation/navigation.component";
import {TranslateModule, TranslateService} from "@ngx-translate/core";
import {PrimeTemplate} from "primeng/api";
import {DropdownModule} from "primeng/dropdown";
import {FormsModule} from "@angular/forms";
import {LanguageConfig} from "../core/config/language.config";
import {HeaderService} from "../core/services/header.service";
import {Title} from "@angular/platform-browser";
import {FaIconComponent} from "@fortawesome/angular-fontawesome";
import {LogoutComponent} from "../logout/logout.component";

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [
    NavigationComponent,
    TranslateModule,
    PrimeTemplate,
    DropdownModule,
    FormsModule,
    FaIconComponent,
    LogoutComponent
  ],
  templateUrl: './header.component.html',
  styleUrl: './header.component.scss'
})
export class HeaderComponent implements OnInit{
  title = "application.title"

  constructor(
    private readonly languageConfig: LanguageConfig,
    private readonly translate: TranslateService,
    private readonly headerService: HeaderService,
    private readonly titleService: Title
  ) {

  }

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
