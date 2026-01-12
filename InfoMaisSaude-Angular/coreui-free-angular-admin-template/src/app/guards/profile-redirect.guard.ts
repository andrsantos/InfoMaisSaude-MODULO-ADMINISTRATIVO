import { inject } from '@angular/core';
import { CanActivateFn, Router, UrlTree } from '@angular/router';
import { AuthService } from '../services/auth/auth.service';
import { Observable } from 'rxjs';
import { ToastrService } from 'ngx-toastr';

export const profileRedirectGuard: CanActivateFn = (route, state) : Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree => {
  
  const router = inject(Router);

  const authService = inject(AuthService);
  
  const role = authService.getUserRole();

  const toastr = inject(ToastrService);

  const possuiClinicaCadastrada = localStorage.getItem("possuiClinica");

  if (role === 'MEDICO') {
    return router.createUrlTree(['/doctor-profile']);
  }

  if(role === 'CLINICA' && possuiClinicaCadastrada){
    return router.createUrlTree(['/update-clinic']);
  } else {
    toastr.info('Você precisa cadastrar sua clínica antes de acessar as outras funcionalidades do sistema', 'Cadastro Pendente');
    router.navigate(['/register-clinic']);
  }
  
  if(role === 'ADMIN'){
    return router.createUrlTree(['/initial-page-admin']);
  }

  return true;
};
