import { ApplicationConfig, isDevMode, inject, provideAppInitializer } from '@angular/core';
import {provideRouter} from '@angular/router';

import {routes} from './app.routes';
import {HTTP_INTERCEPTORS, HttpClient, provideHttpClient, withInterceptorsFromDi} from "@angular/common/http";
import {InterpolatableTranslationObject, TranslateLoader, TranslateModule, TranslateService} from "@ngx-translate/core";
import {TranslateHttpLoader} from "@ngx-translate/http-loader";
import {AuthInterceptor} from "./core/interceptors/auth.interceptor";
import {provideServiceWorker} from '@angular/service-worker';
import {IMAGE_LOADER, ImageLoaderConfig} from "@angular/common";
import {environment} from "../environments/environment";
import {provideAnimationsAsync} from "@angular/platform-browser/animations/async";
import {providePrimeNG} from "primeng/config";
import {MyPreset} from "./mytheme";

/**
 * Creates a new instance of TranslateHttpLoader with the specified HttpClient.
 *
 * @param {HttpClient} http - The HttpClient instance to be used for loading translations.
 *
 * @return {TranslateHttpLoader} A new instance of TranslateHttpLoader configured with the provided HttpClient.
 */
export function TranslateLoaderFactory(http: HttpClient): TranslateHttpLoader {
  return new TranslateHttpLoader(http, './assets/i18n/', '.json');
}

/**
 * Initializes the application by setting the default language for the translation service based on
 * the language preference stored in local storage or falling back to English if not available.
 *
 * @param {TranslateService} translate - The translation service used to set the language.
 * @return {() => Promise<void>} A function that returns a promise, which resolves when the language has been set.
 */
function appInitializerFactory(translate: TranslateService): () => Promise<InterpolatableTranslationObject | undefined> {
  return () => {
    const lang = localStorage.getItem('languageIso') ?? 'en';
    translate.setDefaultLang(lang);
    return translate.use(lang).toPromise();
  };
}

const imageLoader = (config: ImageLoaderConfig) => {
  if(config.isPlaceholder){
    return `${environment.fe_images + 'placeholder/' + config.src}`;
  }
  return `${environment.fe_images + config.src}`;
}


/**
 * `appConfig` is an object that holds configuration settings for an application.
 * It specifies providers for various services such as routing, HTTP client,
 * animations, translation services, and service worker.
 */
export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes), // TODO preload strategy, but with linking routes? Possible? https://medium.com/@adrianfaciu/custom-preloading-strategy-for-angular-modules-b3b5c873681a
    provideHttpClient(withInterceptorsFromDi()),
    provideAnimationsAsync(),
    providePrimeNG({
      inputStyle: 'outlined',
      ripple: true,
      theme: {
        preset: MyPreset,
        options: {
          darkModeSelector: '.p-dark'
        }
      }
    }
    ),
    provideAppInitializer(() => {
        const initializerFn = (appInitializerFactory)(inject(TranslateService));
        return initializerFn();
      }),
    {
      provide : HTTP_INTERCEPTORS,
      useClass: AuthInterceptor,
      multi   : true,
    },
    TranslateModule.forRoot({
      defaultLanguage: localStorage.getItem('languageIso') ?? 'en',
      loader: {
        provide: TranslateLoader,
        useFactory: TranslateLoaderFactory,
        deps: [HttpClient]
      }
    }).providers!,
    provideServiceWorker('ngsw-worker.js', {
            enabled: !isDevMode(),
            registrationStrategy: 'registerWhenStable:30000'
          }),
    {
      provide: IMAGE_LOADER,
      useValue: imageLoader,
    }
  ]
};
