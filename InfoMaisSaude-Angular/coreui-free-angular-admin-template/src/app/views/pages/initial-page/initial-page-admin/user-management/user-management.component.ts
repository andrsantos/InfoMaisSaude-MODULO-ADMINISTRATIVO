import {
  Component,
  ComponentRef,
  Injector,
  OnInit,
  ViewContainerRef,
} from "@angular/core";
import { CommonModule } from "@angular/common";
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
  ButtonDirective,
  SpinnerComponent,
  ModalComponent,
  ModalHeaderComponent,
  ModalBodyComponent,
  ModalFooterComponent,
  ButtonCloseDirective,
  Colors,
} from "@coreui/angular";
import { Router } from "@angular/router";
import { UsuariosServiceService } from "../../../../../services/usuarios/usuarios-service.service";
import { UsuarioResponse } from "../../../../../models/usuarioModels/usuarioResponse";
import { UsuarioDeleteResponse } from "../../../../../models/usuarioModels/usuarioDeleteResponse";
import { ToastrService } from "ngx-toastr";
@Component({
  selector: "app-user-management",
  templateUrl: "./user-management.component.html",
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
    ButtonDirective,
    SpinnerComponent,
    ModalComponent,
    ModalHeaderComponent,
    ModalBodyComponent,
    ModalFooterComponent,
    ButtonCloseDirective,
  ],
})
export class UserManagementComponent implements OnInit {
  public usuarios: UsuarioResponse[] = [];
  public isLoading = true;
  public errorMessage = "";
  public isDeleteModalVisible = false;
  public userIdToDelete: number | null = null;
  public userLoginToDelete: string = "";

  constructor(
    private Router: Router,
    private UsuariosService: UsuariosServiceService,
    private toastr: ToastrService
  ) {}

  ngOnInit(): void {
    const state = history.state as {
      showSuccessToast?: boolean;
      message?: string;
    } | null;

    console.log("State lido do history:", state);

    if (state?.showSuccessToast && state?.message) {
      this.toastr.success(state.message, "Sucesso!");
      history.replaceState(
        { ...history.state, showSuccessToast: undefined, message: undefined },
        ""
      );
    } else {
      console.log("Nenhum state de toast encontrado no history.");
    }
    this.showingUserList();
  }

  goToCreateUser() {
    this.Router.navigate(["/create-user"]);
  }

  showingUserList(): void {
    this.isLoading = true;
    this.errorMessage = "";
    this.UsuariosService.listarUsuarios().subscribe({
      next: (listaDeUsuarios) => {
        this.usuarios = listaDeUsuarios;
        this.isLoading = false;
      },
      error: (erro) => {
        console.error("Erro ao listar usuários:", erro);
        this.errorMessage = "Falha ao carregar a lista de usuários.";
        this.isLoading = false;
      },
    });
  }

  confirmDelete(): void {
    if (this.userIdToDelete === null) {
      console.error("ID do usuário para deletar é nulo.");
      this.closeDeleteModal();
      return;
    }

    this.isLoading = true;
    this.errorMessage = "";

    this.UsuariosService.excluirUsuario(this.userIdToDelete).subscribe({
      next: (response: UsuarioDeleteResponse) => {
        console.log(
          `Usuário ${this.userIdToDelete} excluído. Resposta da API:`,
          response
        );
        this.toastr.success(
          response.mensagemDeResposta || "Usuário excluído com sucesso!",
          "Sucesso"
        );
        this.showingUserList();
        this.closeDeleteModal();
      },
      error: (erro) => {
        console.error("Erro ao excluir usuário:", erro);
        const errorMsg =
          erro.error?.message || erro.error || "Falha ao excluir o usuário.";
        this.errorMessage = errorMsg;
        this.toastr.error(errorMsg, "Erro!");
        this.isLoading = false;
        this.closeDeleteModal();
      },
    });
  }

  openDeleteConfirmationModal(id: number, login: string): void {
    this.userIdToDelete = id;
    this.userLoginToDelete = login;
    this.isDeleteModalVisible = true;
  }

  closeDeleteModal(): void {
    this.isDeleteModalVisible = false;
    this.userIdToDelete = null;
    this.userLoginToDelete = "";
  }
}
