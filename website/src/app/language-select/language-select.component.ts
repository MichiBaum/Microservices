import {Component, inject, OnInit, signal} from '@angular/core';
import {LanguageConfig} from "../core/config/language.config";
import {TranslateModule} from "@ngx-translate/core";
import {DropdownModule} from "primeng/dropdown";
import {SelectButtonModule} from "primeng/selectbutton";
import {Language} from "../core/models/language.model";
import {FormsModule} from "@angular/forms";

@Component({
  selector: 'app-language-select',
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
  private readonly languageConfig = inject(LanguageConfig);

  languages = signal<Language[]>(this.languageConfig.languages);
  selectedLanguage: Language | undefined;


  ngOnInit(): void {
    this.selectedLanguage = this.languageConfig.current
  }

  selectLanguage() {
    if(this.selectedLanguage == undefined)
      return;
    this.languageConfig.setLanguage(this.selectedLanguage);
  }


}
