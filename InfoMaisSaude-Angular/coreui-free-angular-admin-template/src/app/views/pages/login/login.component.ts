import { Component } from "@angular/core";
import { CommonModule, NgStyle } from "@angular/common";
import { IconDirective } from "@coreui/icons-angular";
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
  RowComponent,
} from "@coreui/angular";
import { Router, RouterLink } from "@angular/router";
import {
  Form,
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from "@angular/forms";
import { AuthService } from "../../../services/auth/auth.service";

@Component({
  selector: "app-login",
  templateUrl: "./login.component.html",
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
    CommonModule,
  ],
})
export class LoginComponent {
  loginForm: FormGroup;
  errorMessage: string = "";

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.loginForm = this.fb.group({
      login: ["", [Validators.required]],
      senha: ["", [Validators.required]],
    });
  }

  fazerLogin(): void {
    this.errorMessage = "";
    if (this.loginForm.valid) {
      this.authService.login(this.loginForm.value).subscribe({
        next: (response) => {
          localStorage.setItem("authToken", response.token);
          localStorage.setItem("possuiClinica", JSON.stringify(response.possuiClinicaCadastrada));
          localStorage.setItem("usuarioId", JSON.stringify(response.idUsuario));
          const userRole = this.authService.getUserRole();
          if (userRole === "ADMIN") {
            this.router.navigate(["/initial-page-admin"]);
          } else if (userRole == 'CLINICA'){
            this.router.navigate(["/initial-page"]);
          } else if (userRole === "MEDICO") {
            this.router.navigate(["/initial-page-doctor"]);
          }
          else {
            this.router.navigate(['/register-clinic']);
          }
        },
        error: (error) => {
          console.error("Erro no login", error);
          this.errorMessage = "Credenciais inválidas. Tente novamente.";
        },
      });
    } else {
      this.loginForm.markAllAsTouched();
    }
  }
}
