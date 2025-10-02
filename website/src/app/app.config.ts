import {ApplicationConfig, isDevMode} from '@angular/core';
import {provideRouter} from '@angular/router';
import {routes} from './app.routes';
import {
    provideHttpClient,
    withInterceptors
} from "@angular/common/http";
import {provideTranslateService} from "@ngx-translate/core";
import {provideTranslateHttpLoader} from "@ngx-translate/http-loader";
import {authJwtInterceptor} from "./core/interceptors/auth-jwt.interceptor";
import {provideServiceWorker} from '@angular/service-worker';
import {IMAGE_LOADER, ImageLoaderConfig} from "@angular/common";
import {provideAnimationsAsync} from "@angular/platform-browser/animations/async";
import {providePrimeNG} from "primeng/config";
import {MyPreset} from "./mytheme";
import {EnvironmentConfig} from "./core/config/environment.config";
import {httpErrorInterceptor} from "./core/interceptors/http-error.interceptor";
import {languageInterceptor} from "./core/interceptors/language.interceptor";

export function imageLoaderFactory(environment: EnvironmentConfig): (config: ImageLoaderConfig) => string {
  return (config: ImageLoaderConfig) => {
    if(config.isPlaceholder){
      return `${environment.fe_images() + 'placeholder/' + config.src}`;
    }
    return `${environment.fe_images() + config.src}`;
  }
}

/**
 * `appConfig` is an object that holds configuration settings for an application.
 * It specifies providers for various services such as routing, HTTP client,
 * animations, translation services, and service worker.
 */
export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes), // TODO preload strategy, but with linking routes? Possible? https://medium.com/@adrianfaciu/custom-preloading-strategy-for-angular-modules-b3b5c873681a
    provideHttpClient(
        withInterceptors([
            authJwtInterceptor,
            languageInterceptor,
            httpErrorInterceptor
        ])
    ),
    provideAnimationsAsync(),
    providePrimeNG({
      inputStyle: 'outlined',
      ripple: true,
      theme: {
        preset: MyPreset,
        options: {
          darkModeSelector: '.dark'
        }
      }
    }),
    provideTranslateService({
      loader: provideTranslateHttpLoader({
          prefix:"./assets/i18n/"
          ,suffix: ".json"
      }),
      fallbackLang: 'en',
      lang: 'en'
    }),
    provideServiceWorker(
      'ngsw-worker.js',
      {
        enabled: !isDevMode(),
        registrationStrategy: 'registerWhenStable:30000'
      }
    ),
    {
      provide: IMAGE_LOADER,
      deps: [EnvironmentConfig],
      useFactory: (imageLoaderFactory),
    }
  ]
};
