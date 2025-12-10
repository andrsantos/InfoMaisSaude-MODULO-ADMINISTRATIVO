import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth/auth.service';
import { inject } from '@angular/core';
import { ToastrService } from 'ngx-toastr';


export const clinicaCadastradaGuard: CanActivateFn = (route, state) => {

  const authService = inject(AuthService);
  const router = inject(Router);
  const toastr = inject(ToastrService);

  if(!authService.isAuthenticated()){
    router.navigate(['/login']);
    return false;
  }

  const userRole = authService.getUserRole();
  if(userRole != 'CLINICA'){
    toastr.error('Você não tem permissão para acessar esta página', 'Acesso Negado');
    if(userRole == 'ADMIN'){
      router.navigate(['/initial-page-admin']);
    } else {
      return false;
    }
  }

  if(authService.hasRegisteredClinic()){
    return true;
  } else {
    toastr.info('Você precisa cadastrar sua clínica antes de acessar as outras funcionalidades do sistema', 'Cadastro Pendente');
    router.navigate(['/register-clinic']);
    return false;
  }

};
