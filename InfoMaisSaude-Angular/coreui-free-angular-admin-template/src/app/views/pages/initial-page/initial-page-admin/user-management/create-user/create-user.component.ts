import { Component } from "@angular/core";
import { CommonModule } from "@angular/common";
import {
  ReactiveFormsModule,
  FormBuilder,
  FormGroup,
  Validators,
} from "@angular/forms";
import { Router } from "@angular/router";
import {
  CardComponent,
  CardBodyComponent,
  CardHeaderComponent,
  ColComponent,
  FormDirective,
  FormControlDirective,
  FormLabelDirective,
  RowComponent,
  ButtonDirective,
  FormSelectDirective,
} from "@coreui/angular";
import { UsuarioCreateResponse } from "../../../../../../models/usuarioModels/usuarioCreateResponse";
import { UsuarioCreate } from "../../../../../../models/usuarioModels/usuarioCreate";
import { AuthService } from "../../../../../../services/auth/auth.service";
import { ToastrService } from "ngx-toastr";
import { UsuariosServiceService } from "../../../../../../services/usuarios/usuarios-service.service";

enum UserRole {
  ADMIN = "ADMIN",
  CLINICA = "CLINICA",
  PROFISSIONAL_LIBERAL = "PROFISSIONAL_LIBERAL",
}

@Component({
  selector: "app-create-user",
  templateUrl: "./create-user.component.html",
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    CardComponent,
    CardHeaderComponent,
    CardBodyComponent,
    RowComponent,
    ColComponent,
    FormDirective,
    FormLabelDirective,
    FormControlDirective,
    ButtonDirective,
    FormSelectDirective,
  ],
})
export class CreateUserComponent {
  usuarioForm: FormGroup;
  errorMessage: string = "";
  successMessage: string = "";
  isLoading: boolean = false;
  userRoles = Object.values(UserRole);
  usuarioCreateResponse: UsuarioCreateResponse | null = null;
  usuarioCreate: UsuarioCreate | undefined;

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private UsuarioService: UsuariosServiceService,
    private AuthService: AuthService,
    private toastr: ToastrService
  ) {
    this.usuarioForm = this.fb.group({
      login: ["", [Validators.required]],
      senha: ["", [Validators.required, Validators.minLength(6)]],
      role: ["", Validators.required],
    });
  }

  onSubmit(): void {
    this.errorMessage = "";
    this.successMessage = "";
  }

  cadastrarUsuario(): void {
    this.errorMessage = "";
    this.successMessage = "";
    this.isLoading = true;
    this.usuarioCreateResponse = null;

    if (this.usuarioForm.invalid) {
      console.log("Formulário inválido");
      this.errorMessage =
        "Por favor, preencha todos os campos obrigatórios corretamente.";
      this.usuarioForm.markAllAsTouched();
      this.isLoading = false;
      return;
    }

    const adminLogin = this.AuthService.getLoggedInUserLogin();

    if (!adminLogin) {
      this.errorMessage =
        "Erro: Não foi possível identificar o usuário admin logado. Faça login novamente.";
      this.isLoading = false;
      this.AuthService.logout();
      return;
    }

    const formData = this.usuarioForm.value;
    const payload = {
      ...formData,
      loginUsuarioCriador: adminLogin,
    };
    this.usuarioCreate = payload;

    console.log("Enviando Payload para API:", this.usuarioCreate);

    this.UsuarioService.cadastrarUsuario(this.usuarioCreate).subscribe({
      next: (response: UsuarioCreateResponse) => {
        this.usuarioCreateResponse = response;
        const succesMsg =
          response.mensagemDeResposta || "Usuário cadastrado com sucesso!";
        this.usuarioForm.reset();
        this.isLoading = false;
        this.router.navigate(["/user-management"], {
          state: {
            showSuccessToast: true,
            message: succesMsg,
          },
        });
      },
      error: (erro) => {
        console.error("Erro ao cadastrar usuário:", erro);
        this.errorMessage =
          erro.error?.message ||
          erro.error ||
          "Ocorreu um erro ao cadastrar o usuário. Tente novamente.";
        this.isLoading = false;
      },
    });
  }
}

