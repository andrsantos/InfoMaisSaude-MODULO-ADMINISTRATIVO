import { ApplicationConfig, importProvidersFrom } from '@angular/core';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import {
  provideRouter,
  withEnabledBlockingInitialNavigation,
  withHashLocation,
  withInMemoryScrolling,
  withRouterConfig,
  withViewTransitions,
  RouterModule,
} from '@angular/router';
import { provideToastr } from 'ngx-toastr';
import { DropdownModule, SidebarModule } from '@coreui/angular';
import { IconSetService } from '@coreui/icons-angular';
import { routes } from './app.routes';
import { authInterceptor } from './interceptors/auth.interceptor';

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes,
      withRouterConfig({
        onSameUrlNavigation: 'reload'
      }),
      withInMemoryScrolling({
        scrollPositionRestoration: 'top',
        anchorScrolling: 'enabled'
      }),
      withEnabledBlockingInitialNavigation(),
      withViewTransitions(),
      withHashLocation()
    ),
  provideHttpClient(withInterceptors([
    authInterceptor
  ])),
  importProvidersFrom(RouterModule, SidebarModule, DropdownModule),
    IconSetService,
    provideAnimationsAsync(),
    provideToastr({
      timeOut: 5000, 
      positionClass: 'toast-top-right', 
      preventDuplicates: true, 
      closeButton: true, 
      progressBar: true 
    }),
  ]
};
