import {Component, inject, OnInit, signal} from '@angular/core';
import {LanguageConfig} from "../core/config/language.config";
import {TranslateModule} from "@ngx-translate/core";
import {Language} from "../core/models/language.model";
import {Listbox} from "primeng/listbox";
import {FormsModule} from "@angular/forms";

@Component({
  selector: 'app-language-select',
    imports: [
        TranslateModule,
        Listbox,
        FormsModule
    ],
  templateUrl: './language-select.component.html',
  styleUrl: './language-select.component.css'
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


