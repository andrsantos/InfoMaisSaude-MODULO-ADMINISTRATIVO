import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth/auth.service';
import { ToastrService } from 'ngx-toastr';
import { inject } from '@angular/core';

export const clinicaNaoCadastradaGuard: CanActivateFn = (route, state) => {
  
  const authService = inject(AuthService);
  const router = inject(Router);
  const toastr = inject(ToastrService);

  if(!authService.isAuthenticated()){
    router.navigate(['/login']);
    return false;
  }

  if(authService.getUserRole() != 'CLINICA'){
    toastr.error('Você não tem permissão para acessar esta página', 'Acesso Negado');
    if(authService.getUserRole() ===  'ADMIN'){
      router.navigate(['/initial-page-admin']);
    } else {
      return false;
  }
}

  if(!authService.hasRegisteredClinic()){
    return true;
  } else {
    return false
  }
}
