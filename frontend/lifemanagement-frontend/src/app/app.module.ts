import {APP_BASE_HREF, registerLocaleData} from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import {HttpClient} from '@angular/common/http';
import localeDe from '@angular/common/locales/de';
import localeEn from '@angular/common/locales/en';
import {ErrorHandler, NgModule} from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import { ServiceWorkerModule } from '@angular/service-worker';
import {MissingTranslationHandler, TranslateLoader, TranslateModule} from '@ngx-translate/core';
import {TranslateHttpLoader} from '@ngx-translate/http-loader';
import {ButtonModule, DialogModule, MessageService, TabViewModule, ToastModule, TreeModule} from 'primeng';
import { environment } from '../environments/environment';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import {AppService} from './app.service';
import {appInitializerProviders} from './core/app-initializer-provider';
import {CoreModule} from './core/core.module';
import {GlobalErrorHandler} from './core/error-handlers/global-error.handler';
import {TranslateErrorHandler} from './core/error-handlers/translate-error.handler';
import {LanguageConfig} from './core/language.config';
import {PipeModule} from './core/pipes/pipe.module';
import {httpInterceptorProviders} from './core/security/http-interceptors';
import { AuthGuardService as AuthGuard } from './core/services/auth-guard.service';
import {HeaderModule} from './header/header.module';
import { HelpDialogComponent } from './help-dialog/help-dialog.component';
import {HelpDialogModule} from './help-dialog/help-dialog.module';
import {HomeModule} from './home/home.module';
import {LoginModule} from './login/login.module';
import {LoggingModule} from './logs/logging.module';
import {NavigationModule} from './navigation/navigation.module';
import { ToastMessageComponent } from './toast-message/toast-message.component';
import {UsersettingsModule} from './usersettings/usersettings.module';
import { CheckListComponent } from './check-list/check-list.component';

export function TranslateLoaderFactory(http: HttpClient) {
  return new TranslateHttpLoader(http, './assets/i18n/', '.json');
}

registerLocaleData(localeDe);
registerLocaleData(localeEn);

@NgModule({
  declarations: [
    AppComponent,
    ToastMessageComponent,
    HelpDialogComponent,
    CheckListComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    ServiceWorkerModule.register('ngsw-worker.js', {enabled: environment.production}),
    CoreModule,
    LoginModule,
    HomeModule,
    HeaderModule,
    NavigationModule,
    LoggingModule,
    HelpDialogModule,
    PipeModule,
    UsersettingsModule,
    BrowserAnimationsModule,
    HttpClientModule,
    TranslateModule.forRoot({
      loader: {
        provide: TranslateLoader,
        useFactory: TranslateLoaderFactory,
        deps: [HttpClient],
      },
      missingTranslationHandler:
        {
          provide: MissingTranslationHandler,
          useClass: TranslateErrorHandler,
          deps: [AppService]
        }
    }),
    ToastModule,
    ButtonModule,
    DialogModule,
    TabViewModule,
    TreeModule,
  ],
  providers: [
    LanguageConfig,
    httpInterceptorProviders,
    appInitializerProviders,
    [AuthGuard],
    MessageService,
    {
      provide: ErrorHandler,
      useClass: GlobalErrorHandler
    },
    {
      provide: APP_BASE_HREF,
      useValue: environment.base_href
    }
  ],
  bootstrap: [
    AppComponent
  ],
})
export class AppModule {}
