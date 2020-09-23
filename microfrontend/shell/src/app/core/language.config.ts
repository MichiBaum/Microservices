import {EventEmitter, Injectable, Output} from '@angular/core';
import {TranslateService} from '@ngx-translate/core';
import {ILanguage} from './models/language.model';

export const languages: ILanguage[] = [
  {
    name: 'language.english',
    isoCode: 'en'
  } as ILanguage
  , {
    name: 'language.german',
    isoCode: 'de'
  } as ILanguage
];

export const defaultLanguage: ILanguage = {
  name: 'language.english',
  isoCode: 'en'
} as ILanguage;

@Injectable()
export class LanguageConfig {

  localStorageKey = 'languageIso';
  languages: ILanguage[];
  current: ILanguage;
  defaultLanguage: ILanguage;

  @Output() languageChanged: EventEmitter<ILanguage> = new EventEmitter<ILanguage>();

  constructor(
    public translate: TranslateService
  ) {
    this.languages = languages;
    this.defaultLanguage = defaultLanguage;
    this.translate.setDefaultLang(defaultLanguage.isoCode);
    this.setInitLanguage();
  }

  setLanguage = (language: ILanguage) => {
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

  private getBrowserLanguage = (): ILanguage => {
    const browserLanguage = this.translate.getBrowserLang();
    return this.getFromList(browserLanguage);
  }

  private fromLocalStorage = (): ILanguage => {
    const language = localStorage.getItem(this.localStorageKey);
    if (language) {
      return this.getFromList(language);
    }
  }

  private getFromList = (isoCode: string): ILanguage => {
    return this.languages.find((curr) => curr.isoCode === isoCode);
  }
}
