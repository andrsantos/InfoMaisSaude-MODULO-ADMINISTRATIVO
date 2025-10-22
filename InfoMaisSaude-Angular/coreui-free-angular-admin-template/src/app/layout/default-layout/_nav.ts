import { INavData } from '@coreui/angular';

export const navItems: INavData[] = [

  {
    name: 'Página Inicial',
    url: '/dashboard',
    iconComponent: { name: 'cil-home' }
  },
  
  {
    name: 'Ver Perfil',
    url: '/dashboard',
    iconComponent: { name: 'cilUser' }
  },
  {
    name: 'Sobre o sistema',
    url: 'https://coreui.io/angular/docs/',
    iconComponent: { name: 'cil-description' },
    attributes: { target: '_blank' }
  }
];
