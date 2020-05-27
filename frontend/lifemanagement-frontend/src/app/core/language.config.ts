import {EventEmitter, Injectable, Output} from '@angular/core';
import {TranslateService} from '@ngx-translate/core';
import {Language} from './models/language.model';

export const languages: Language[] = [
  {
    name: 'language.english',
    isoCode: 'en'
  } as Language
  , {
    name: 'language.german',
    isoCode: 'de'
  } as Language
];

export const defaultLanguage: Language = {
  name: 'language.english',
  isoCode: 'en'
} as Language;

@Injectable()
export class LanguageConfig {

  localStorageKey = 'languageIso';
  languages: Language[];
  current: Language;
  defaultLanguage: Language;

  @Output() languageChanged: EventEmitter<Language> = new EventEmitter<Language>();

  constructor(
    public translate: TranslateService
  ) {
    this.languages = languages;
    this.defaultLanguage = defaultLanguage;
    this.translate.setDefaultLang(defaultLanguage.isoCode);
    this.setInitLanguage();
  }

  setLanguage = (language: Language) => {
    localStorage.setItem(this.localStorageKey, language.isoCode);
    this.current = language;
    this.translate.use(language.isoCode);
    this.languageChanged.emit(language);
  }

  private setInitLanguage = (): void => {
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

    this.setLanguage(this.languages[0]);
  }

  private getBrowserLanguage = (): Language => {
    const browserLanguage = this.translate.getBrowserLang();
    return this.getFromList(browserLanguage);
  }

  private fromLocalStorage = (): Language => {
    const language = localStorage.getItem(this.localStorageKey);
    if (language) {
      return this.getFromList(language);
    }
  }

  private getFromList = (isoCode: string): Language => {
    return this.languages.find((curr) => curr.isoCode === isoCode);
  }
}
