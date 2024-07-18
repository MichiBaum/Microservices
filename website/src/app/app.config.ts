import {APP_INITIALIZER, ApplicationConfig} from '@angular/core';
import { provideRouter } from '@angular/router';

import { routes } from './app.routes';
import {HttpClient, provideHttpClient} from "@angular/common/http";
import {LanguageConfig} from "./core/config/language.config";
import {TranslateLoader, TranslateModule, TranslatePipe, TranslateService} from "@ngx-translate/core";
import {TranslateHttpLoader} from "@ngx-translate/http-loader";
import {provideAnimations} from "@angular/platform-browser/animations";

export function TranslateLoaderFactory(http: HttpClient) {
  return new TranslateHttpLoader(http, './assets/i18n/', '.json');
}

function appInitializerFactory(translate: TranslateService) {
  return () => {
    var lang = localStorage.getItem('languageIso') || 'en'
    translate.setDefaultLang(lang);
    return translate.use(lang).toPromise();
  };
}

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes),
    provideHttpClient(),
    provideAnimations(),
    {
      provide: APP_INITIALIZER,
      useFactory: appInitializerFactory,
      deps: [TranslateService],
      multi: true
    },
    TranslateModule.forRoot({
      defaultLanguage: localStorage.getItem('languageIso') || 'en',
      loader: {
        provide: TranslateLoader,
        useFactory: TranslateLoaderFactory,
        deps: [HttpClient]
      }
    }).providers!,
  ]
};
