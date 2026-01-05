import { Routes } from '@angular/router';
import { authGuard } from './guards/auth.guard';
import { loginGuard } from './guards/login.guard';
import { clinicaNaoCadastradaGuard } from './guards/clinica-nao-cadastrada.guard';
import { clinicaCadastradaGuard } from './guards/clinica-cadastrada.guard';
import { roleRedirectGuard } from './guards/role-redirect.guard';
import { profileRedirectGuard } from './guards/profile-redirect.guard';
export const routes: Routes = [
  {
    path: '',
    loadComponent: () => import('./layout').then(m => m.DefaultLayoutComponent),
    data: {
      title: 'Home'
    },
    canActivate: [authGuard],
    children: [
      {
        path:'',
        pathMatch:'full',
        canActivate:[roleRedirectGuard],
        loadComponent: () => import('./views/pages/initial-page/initial-page/initial-page.component')
        .then(m => m.InitialPageComponent)      
      },
      {
        path:'profile-redirect',
        pathMatch:'full',  
        canActivate:[profileRedirectGuard],
        loadComponent: () => import('./views/pages/initial-page/initial-page/initial-page.component')
        .then(m => m.InitialPageComponent)

      },
      {
        path:'initial-page',
        loadComponent: () => import('./views/pages/initial-page/initial-page/initial-page.component').then(m => m.InitialPageComponent),
        canActivate: [clinicaCadastradaGuard]
      },
      {
        path:'register-clinic',
        loadComponent: () => import('./views/pages/initial-page/initial-page/register-clinic/register-clinic.component').then(m => m.RegisterClinicComponent),
        canActivate: [clinicaNaoCadastradaGuard]      
      },
      {
        path: 'update-clinic',
        loadComponent: () => import('./views/pages/initial-page/initial-page/update-clinic/update-clinic.component').then(m => m.UpdateClinicComponent)
      },
      {
        path:'register-doctor',
        loadComponent: () => import('./views/pages/initial-page/initial-page/register-doctor/register-doctor.component').then(m => m.RegisterDoctorComponent),
        canActivate: [clinicaCadastradaGuard]
      },
      {
        path:'doctor-profile',
        loadComponent: () => import('./views/pages/initial-page/doctor-profile/doctor-profile.component').then(m => m.DoctorProfileComponent),
      },
      {
        path:'doctors-list',
        loadComponent: () => import('./views/pages/initial-page/initial-page/doctors-list/doctors-list.component').then(m => m.DoctorsListComponent),
        canActivate: [clinicaCadastradaGuard]
      },
      {
        path: 'clinic-requests',
        loadComponent: () => import('./views/pages/initial-page/clinic-requests/clinic-requests.component').then(m => m.ClinicRequestsComponent),
        
      },
      {
        path: 'admin-requests',
        loadComponent: () => import('./views/pages/initial-page/initial-page-admin/admin-requests/admin-requests.component').then(m => m.AdminRequestsComponent),
      },
      {
        path:'appointments',
        loadComponent: () => import('./views/pages/initial-page/appointments/appointments.component').then(m => m.AppointmentsComponent),
      },
      {
        path:'clinic-appointments',
        loadComponent: () => import('./views/pages/initial-page/initial-page/clinic-appointments/clinic-appointments.component').then(m => m.ClinicAppointmentsComponent),
      },
      {
        path:'medicos/agenda/:id',
        loadComponent: () => import('./views/pages/initial-page/initial-page/doctors-list/doctor-schedule/doctor-schedule.component').then(m => m.DoctorScheduleComponent),
      },
      {
        path:'medicos/solicitacoes',
        loadComponent: () => import('./views/pages/initial-page/doctor-requests/my-requests.component').then(m => m.MyRequestsComponent),
      },
      {
        path: 'initial-page-admin',
        loadComponent: () => import('./views/pages/initial-page/initial-page-admin/initial-page-admin.component').then(m => m.InitialPageAdminComponent),
      },
      {
        path: 'initial-page-doctor',
        loadComponent: () => import('./views/pages/initial-page/initial-page-doctor/initial-page-doctor.component').then(m => m.InitialPageDoctorComponent),
      },
      {
        path:'user-management',
        loadComponent: () => import('./views/pages/initial-page/initial-page-admin/user-management/user-management.component').then(m => m.UserManagementComponent),
      },
      {
        path:'create-user',
        loadComponent: () => import('./views/pages/initial-page/initial-page-admin/user-management/create-user/create-user.component').then(m => m.CreateUserComponent),
      },
      {
        path:'clinic-management',
        loadComponent: () => import('./views/pages/initial-page/initial-page-admin/clinic-management/clinic-management.component').then(m => m.ClinicManagementComponent),
      },
      {
        path:'create-user',
        loadComponent: () => import('./views/pages/initial-page/initial-page-admin/user-management/create-user/create-user.component').then(m => m.CreateUserComponent),
      },
      {
        path: 'dashboard',
        loadChildren: () => import('./views/dashboard/routes').then((m) => m.routes)
      },
      {
        path: 'theme',
        loadChildren: () => import('./views/theme/routes').then((m) => m.routes)

      },
      {
        path: 'base',
        loadChildren: () => import('./views/base/routes').then((m) => m.routes)

      },
      {
        path: 'buttons',
        loadChildren: () => import('./views/buttons/routes').then((m) => m.routes)

      },
      {
        path: 'forms',
        loadChildren: () => import('./views/forms/routes').then((m) => m.routes)

      },
      {
        path: 'icons',
        loadChildren: () => import('./views/icons/routes').then((m) => m.routes)

      },
      {
        path: 'notifications',
        loadChildren: () => import('./views/notifications/routes').then((m) => m.routes)

      },
      {
        path: 'widgets',
        loadChildren: () => import('./views/widgets/routes').then((m) => m.routes)

      },
      {
        path: 'charts',
        loadChildren: () => import('./views/charts/routes').then((m) => m.routes)

      },
      {
        path: 'pages',
        loadChildren: () => import('./views/pages/routes').then((m) => m.routes)

      }
    ]
  },
  {
    path: '404',
    loadComponent: () => import('./views/pages/page404/page404.component').then(m => m.Page404Component),
    data: {
      title: 'Page 404'
    }
  },
  {
    path: '500',
    loadComponent: () => import('./views/pages/page500/page500.component').then(m => m.Page500Component),
    data: {
      title: 'Page 500'
    }
  },
  {
    path: 'login',
    loadComponent: () => import('./views/pages/login/login.component').then(m => m.LoginComponent),
    data: {
      title: 'Login Page'
    },
    canActivate: [loginGuard]
  },
  {
    path: 'register',
    loadComponent: () => import('./views/pages/register/register.component').then(m => m.RegisterComponent),
    data: {
      title: 'Register Page'
    },
    canActivate:[loginGuard]
  },
  { path: '**', redirectTo: 'dashboard' }
];
