import {LOCATION_INITIALIZED} from '@angular/common';
import {APP_INITIALIZER, Injector} from '@angular/core';
import {TranslateService} from '@ngx-translate/core';
import {defaultLanguage} from './language.config';

export const appInitializerProviders = [
  {provide: APP_INITIALIZER, useFactory: appInitializerFactory, deps: [TranslateService, Injector], multi: true}
];

// TODO better solution?
export function appInitializerFactory(translate: TranslateService, injector: Injector) {
  return () => new Promise<any>((resolve: any) => {
    const locationInitialized = injector.get(LOCATION_INITIALIZED, Promise.resolve(null));
    locationInitialized.then(() => {
      const langToSet = localStorage.getItem('languageIso') || defaultLanguage.isoCode;
      translate.setDefaultLang(langToSet);
      translate.use(langToSet).subscribe(() => {
      }, (error) => {
        console.error(`Problem with '${langToSet}' language initialization.'`);
        console.error(error);
      }, () => {
        resolve(null);
      });
    });
  });
}
