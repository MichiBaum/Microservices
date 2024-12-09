import {Component, OnInit} from '@angular/core';
import {LanguageConfig} from "../core/config/language.config";
import {TranslateModule, TranslateService} from "@ngx-translate/core";
import {DropdownModule} from "primeng/dropdown";
import {SelectButtonModule} from "primeng/selectbutton";
import {Language} from "../core/models/language.model";
import {FormsModule} from "@angular/forms";

@Component({
  selector: 'app-language-select',
  standalone: true,
  imports: [
    DropdownModule,
    SelectButtonModule,
    TranslateModule,
    FormsModule
  ],
  templateUrl: './language-select.component.html',
  styleUrl: './language-select.component.scss'
})
export class LanguageSelectComponent implements OnInit{

  languages: Language[] | undefined;
  selectedLanguage: Language | undefined;

  constructor(
    private readonly languageConfig: LanguageConfig,
    private readonly translate: TranslateService
  ) { }

  ngOnInit(): void {
    this.languages = this.languageConfig.languages;
  }

  selectLanguage(event: any) {
    if(this.selectedLanguage == undefined)
      return;
    this.languageConfig.setLanguage(this.selectedLanguage);
  }


}
