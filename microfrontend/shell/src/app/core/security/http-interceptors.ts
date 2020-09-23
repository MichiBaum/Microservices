import {HTTP_INTERCEPTORS} from '@angular/common/http';
import {AddLanguageHeaderInterceptor} from '../add_language_header.interceptor';
import {AuthInterceptor} from './auth.interceptor';

export const httpInterceptorProviders = [
  { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true },
  { provide: HTTP_INTERCEPTORS, useClass: AddLanguageHeaderInterceptor, multi: true }
];
