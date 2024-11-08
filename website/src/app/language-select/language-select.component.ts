import {Component, OnInit} from '@angular/core';
import {PrimeNgBase} from "../core/models/primeng-base.model";
import {LanguageConfig} from "../core/config/language.config";
import {TranslateService} from "@ngx-translate/core";
import {DropdownModule} from "primeng/dropdown";

@Component({
  selector: 'app-language-select',
  standalone: true,
  imports: [
    DropdownModule
  ],
  templateUrl: './language-select.component.html',
  styleUrl: './language-select.component.scss'
})
export class LanguageSelectComponent implements OnInit{

  languages: PrimeNgBase[] | undefined;

  constructor(private languageConfig: LanguageConfig, private translate: TranslateService) { }

  ngOnInit(): void {
    this.languages = this.initLanguages();
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
