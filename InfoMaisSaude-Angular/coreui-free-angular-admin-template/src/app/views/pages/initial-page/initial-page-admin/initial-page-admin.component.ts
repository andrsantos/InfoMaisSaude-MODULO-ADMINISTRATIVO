import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router'; 

import { 
  RowComponent, 
  ColComponent, 
  CardComponent, 
  CardBodyComponent, 
  CardImgDirective, 
  CardTitleDirective, 
  CardTextDirective, 
  ButtonDirective 
} from '@coreui/angular';

@Component({
  selector: 'app-initial-page-admin',
  templateUrl: './initial-page-admin.component.html',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    RowComponent, 
    ColComponent, 
    CardComponent, 
    CardBodyComponent, 
    CardImgDirective, 
    CardTitleDirective, 
    CardTextDirective, 
    ButtonDirective
  ]
})
export class InitialPageAdminComponent {
  
  constructor( private Router: Router) {

  }

  goToUserManagement() {
    this.Router.navigate(['/user-management']);
  }
  goToClinicManagement() {
    this.Router.navigate(['/clinic-management']);
  }

  
}