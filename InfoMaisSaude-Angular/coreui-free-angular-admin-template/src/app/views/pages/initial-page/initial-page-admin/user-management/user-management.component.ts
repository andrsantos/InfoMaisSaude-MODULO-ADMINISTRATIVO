import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common'; 

import {
  CardBodyComponent,
  CardComponent,
  CardHeaderComponent,
  ColComponent,
  RowComponent,
  TableDirective, 
  TableColorDirective,
  TableActiveDirective,
  BorderDirective,
  AlignDirective,
  TextColorDirective,
  ButtonDirective
} from '@coreui/angular';
import { Router } from '@angular/router';
interface User {
  id: number;
  login: string;
  role: 'ADMIN' | 'CLINICA' | 'PROFISSIONAL_LIBERAL'; // Tipos definidos
  creationDate: Date;
}

@Component({
  selector: 'app-user-management',
  templateUrl: './user-management.component.html',
  standalone: true,
  imports: [
    CommonModule,
    CardBodyComponent,
    CardComponent,
    CardHeaderComponent,
    ColComponent,
    RowComponent,
    TableDirective,
    TableColorDirective,
    TableActiveDirective,
    BorderDirective,
    AlignDirective,
    TextColorDirective,
    ButtonDirective
  ]
})
export class UserManagementComponent implements OnInit {

  public users: User[] = [];

  constructor(private Router: Router) { }

  ngOnInit(): void {
    this.users = [
      { id: 1, login: 'admin@email.com', role: 'ADMIN', creationDate: new Date(2025, 9, 20, 10, 30) }, // Mês é 0-indexado (9 = Outubro)
      { id: 2, login: 'clinica_a@email.com', role: 'CLINICA', creationDate: new Date(2025, 9, 21, 8, 0) },
      { id: 3, login: 'dr.joao@email.com', role: 'PROFISSIONAL_LIBERAL', creationDate: new Date(2025, 9, 21, 9, 15) },
      { id: 4, login: 'clinica_b@email.com', role: 'CLINICA', creationDate: new Date(2025, 9, 21, 11, 0) }
    ];
  }

  goToCreateUser() {
    this.Router.navigate(['/create-user']);
  }
}