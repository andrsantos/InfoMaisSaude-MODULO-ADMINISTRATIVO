import { inject } from '@angular/core';
import { CanActivateFn, Router, UrlTree } from '@angular/router';
import { map, Observable, of } from 'rxjs';
import { AuthService } from '../services/auth/auth.service';
import { ClinicasService } from '../services/clinicas/clinicas.service';

export const roleRedirectGuard: CanActivateFn = (route, state): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree => {
  
  const router = inject(Router);
  const authService = inject(AuthService);
  const clinicaService = inject(ClinicasService);

  const role = authService.getUserRole(); 

  if (role === 'ADMIN') {
    return router.createUrlTree(['/initial-page-admin']);
  }

  if (role === 'MEDICO') {
    console.log("CHEGOU NO ROLE REDIRECT GUARD PARA MEDICO");
    return router.createUrlTree(['/initial-page-doctor']);
  }

  if (role === 'CLINICA') {
    if(clinicaService.verificarCadastro()){
      return router.createUrlTree(['/initial-page']);
    } else {
      return router.createUrlTree(['/register-clinic']);
    }
  }

  return router.createUrlTree(['/login']);
};