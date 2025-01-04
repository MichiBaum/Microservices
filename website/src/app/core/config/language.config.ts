import {inject, Injectable} from '@angular/core';
import {TranslateService} from '@ngx-translate/core';
import {Language} from '../models/language.model';
import {Subject} from "rxjs";

export const languages: Language[] = [
  {
    name: 'languages.english',
    isoCode: 'en'
  } as Language,
  {
    name: 'languages.german',
    isoCode: 'de'
  } as Language
];

export const defaultLanguage: Language = {
  name: 'languages.english',
  isoCode: 'en'
} as Language;

@Injectable({ providedIn: 'root' })
export class LanguageConfig {
  private readonly translate = inject(TranslateService);


  localStorageKey = 'languageIso';
  languages: Language[] = languages;
  current: Language | undefined;
  defaultLanguage: Language = defaultLanguage;

  languageChanged = new Subject<Language>;


  constructor() {
    this.setInitLanguage();
  }

  setLanguage = (language: Language) => {
    this.current = language;
    this.translate.use(language.isoCode);
    this.translate.setDefaultLang(language.isoCode);
    localStorage.setItem(this.localStorageKey, language.isoCode);
    this.languageChanged.next(language);
  }

  private setInitLanguage = (): void => {
    let availableLangs = this.languages.map((language) => language.isoCode);
    this.translate.addLangs(availableLangs)

    let language = this.fromLocalStorage();
    if (language) {
      this.setLanguage(language);
      return;
    }

    language = this.getBrowserLanguage();
    if (language) {
      this.setLanguage(language);
      return;
    }

    this.setLanguage(this.defaultLanguage);
  }

  private getBrowserLanguage = (): Language => {
    const browserLanguage = this.translate.getBrowserLang();
    return this.getFromList(browserLanguage);
  }

  private fromLocalStorage = (): Language => {
    const language = localStorage.getItem(this.localStorageKey);
    return this.getFromList(language);
  }

  private getFromList = (isoCode: string | undefined | null): Language => {
    if(isoCode == undefined)
      return defaultLanguage;
    return <Language>this.languages.find((curr) => curr.isoCode === isoCode);
  }
}
