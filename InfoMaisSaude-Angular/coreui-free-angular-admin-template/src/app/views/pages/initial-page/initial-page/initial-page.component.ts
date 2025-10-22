import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router'; // Para que os botões possam ter links

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
  selector: 'app-initial-page',
  templateUrl: './initial-page.component.html',
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
export class InitialPageComponent {
  
  constructor( private Router: Router) {

  }

  goToRegisterClinic(): void {
    this.Router.navigate(['/register-clinic']);
   }
  goToRegisterDoctor(): void {
    this.Router.navigate(['/register-doctor']);
  }
}