import { INavData } from '@coreui/angular';

export const navItems: INavData[] = [

  {
    name: 'Página Inicial',
    url: '/',
    iconComponent: { name: 'cil-home' }
  },
  
  {
    name: 'Ver Perfil',
    url: '/profile-redirect',
    iconComponent: { name: 'cilUser' }
  }

];
