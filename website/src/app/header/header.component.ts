import {AfterContentInit, AfterViewInit, Component, OnInit} from '@angular/core';
import {NavigationComponent} from "./navigation/navigation.component";
import {TranslateModule, TranslateService} from "@ngx-translate/core";
import {PrimeTemplate} from "primeng/api";
import {animate, state, style, transition, trigger} from "@angular/animations";
import {DropdownModule} from "primeng/dropdown";
import {FormsModule} from "@angular/forms";
import {PrimeNgBase} from "../core/models/primeng-base.model";
import {LanguageConfig, languages} from "../core/config/language.config";

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [
    NavigationComponent,
    TranslateModule,
    PrimeTemplate,
    DropdownModule,
    FormsModule
  ],
  templateUrl: './header.component.html',
  styleUrl: './header.component.scss'
})
export class HeaderComponent implements OnInit{

  languages: PrimeNgBase[] | undefined;

  constructor(private languageConfig: LanguageConfig, private translate: TranslateService) {
    this.languages = this.initLanguages();
  }

  ngOnInit(): void {


  }

  initLanguages = () => {
    return this.languageConfig.languages.map((lang) => {
      return {label: this.translate.instant(lang.name), field: lang.isoCode, value: lang.isoCode} as PrimeNgBase;
    });
  }

  selectLanguage = (event: any) => {
    const language = this.languageConfig.languages.find((lang) => lang.isoCode === event.value);
    if(language == undefined)
      return;
    this.languageConfig.setLanguage(language);
  }

}
