import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { inject } from '@angular/core';

export const loginGuard: CanActivateFn = (route, state) => {
const authService = inject(AuthService); 
  const router = inject(Router);

  if (authService.isAuthenticated()) {
    console.log('Usuário já logado. Redirecionando para /initial-page');
    router.navigate(['/initial-page']);
    return false;
  } else {
    return true;
  }};
