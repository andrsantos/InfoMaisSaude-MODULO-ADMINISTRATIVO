import { Component } from '@angular/core';
import { CommonModule } from '@angular/common'; 
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms'; 
import { Router } from '@angular/router'; 
import {
  CardComponent, CardBodyComponent, CardHeaderComponent, ColComponent,
  FormDirective, FormControlDirective, FormLabelDirective, RowComponent,
  ButtonDirective, FormSelectDirective 
} from '@coreui/angular';

enum UserRole {
  ADMIN = 'ADMIN',
  CLINICA = 'CLINICA',
  PROFISSIONAL_LIBERAL = 'PROFISSIONAL_LIBERAL'
}

@Component({
  selector: 'app-create-user',
  templateUrl: './create-user.component.html',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    CardComponent, CardHeaderComponent, CardBodyComponent, RowComponent, ColComponent,
    FormDirective, FormLabelDirective, FormControlDirective, ButtonDirective,
    FormSelectDirective 
  ]
})
export class CreateUserComponent {

  usuarioForm: FormGroup;
  errorMessage: string = '';
  successMessage: string = '';

  userRoles = Object.values(UserRole);

  constructor(
    private fb: FormBuilder,
    private router: Router
  ) {
    this.usuarioForm = this.fb.group({
      login: ['', [Validators.required, Validators.email]],
      senha: ['', [Validators.required, Validators.minLength(6)]], 
      role: ['', Validators.required] 
    });
  }

  onSubmit(): void {
    this.errorMessage = '';
    this.successMessage = '';

    if (this.usuarioForm.valid) {
      console.log('Formulário válido. Dados:', this.usuarioForm.value);
      this.successMessage = 'Usuário pronto para ser criado (ver console)!';
    } else {
      console.log('Formulário inválido');
      this.errorMessage = 'Por favor, preencha todos os campos corretamente.';
      this.usuarioForm.markAllAsTouched(); 
    }
  }
}