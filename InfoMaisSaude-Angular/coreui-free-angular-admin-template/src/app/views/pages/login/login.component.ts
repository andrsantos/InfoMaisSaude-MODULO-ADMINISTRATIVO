import { Component } from '@angular/core';
import { CommonModule, NgStyle } from '@angular/common';
import { IconDirective } from '@coreui/icons-angular';
import {
  ButtonDirective,
  CardBodyComponent,
  CardComponent,
  CardGroupComponent,
  ColComponent,
  ContainerComponent,
  FormControlDirective,
  FormDirective,
  InputGroupComponent,
  InputGroupTextDirective,
  RowComponent
} from '@coreui/angular';
import { Router, RouterLink } from '@angular/router';
import { Form, FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  imports: [
    RouterLink, 
    ContainerComponent, 
    RowComponent, 
    ColComponent, 
    CardGroupComponent, 
    CardComponent, 
    CardBodyComponent, 
    FormDirective, 
    InputGroupComponent, 
    InputGroupTextDirective, 
    IconDirective, 
    FormControlDirective, 
    ButtonDirective, 
    NgStyle, 
    ReactiveFormsModule,
    CommonModule]
})
export class LoginComponent {
 
  loginForm: FormGroup;
  errorMessage: string = '';

  constructor(private fb: FormBuilder, private authService: AuthService, private router: Router) {
      this.loginForm = this.fb.group({
        login: ['', [Validators.required]] ,
        senha: ['', [Validators.required]] 
      });
    }

  fazerLogin(): void {
    this.errorMessage = '';
    if(this.loginForm.valid){
      this.authService.login(this.loginForm.value).subscribe({
        next: (response) => {
          console.log("Login bem-sucedido", response);
          localStorage.setItem('authToken', response.token);
          this.router.navigate(['/initial-page-admin']);
        },
        error: (error) => {
          console.error("Erro no login", error);
          this.errorMessage = 'Credenciais inválidas. Tente novamente.';
        }
      });
    } else {
        this.loginForm.markAllAsTouched();
      }
  }

  }




