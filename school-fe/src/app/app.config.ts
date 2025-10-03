import { provideTranslateHttpLoader } from '@ngx-translate/http-loader';
import { ApplicationConfig, provideBrowserGlobalErrorListeners, provideZonelessChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';

import { routes } from './app.routes';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { provideTranslateService } from '@ngx-translate/core';
import { authInterceptor } from './core/interceptors/auth-interceptor';
import { unwrapResponseInterceptor } from './core/interceptors/unwrap-response-interceptor';
import { responseInterceptor } from './core/interceptors/response-interceptor';

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideZonelessChangeDetection(),
    provideRouter(routes),
    provideHttpClient(withInterceptors([
      authInterceptor,
      unwrapResponseInterceptor,
      responseInterceptor,
    ])),
    provideTranslateService({
      loader: provideTranslateHttpLoader({
        prefix: 'assets/i18n/',
        suffix: '.json'
      }),
      fallbackLang: 'en'
    })
  ]
};
