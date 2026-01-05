import { inject } from '@angular/core';
import { CanActivateFn, Router, UrlTree } from '@angular/router';
import { AuthService } from '../services/auth/auth.service';
import { Observable } from 'rxjs';

export const profileRedirectGuard: CanActivateFn = (route, state) : Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree => {
  
  const router = inject(Router);

  const authService = inject(AuthService);
  
  const role = authService.getUserRole();

  if (role === 'MEDICO') {
    return router.createUrlTree(['/doctor-profile']);
  }

  if(role === 'CLINICA'){
    return router.createUrlTree(['/update-clinic']);
  }
  if(role === 'ADMIN'){
    return router.createUrlTree(['/initial-page-admin']);
  }

  return true;
};
