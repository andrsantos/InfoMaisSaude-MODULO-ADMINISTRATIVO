import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common'; 
import { Router, RouterLink } from '@angular/router'; 
import {
  CardBodyComponent, CardComponent, CardHeaderComponent, ColComponent,
  RowComponent, TableDirective, TableColorDirective, TableActiveDirective,
  BorderDirective, AlignDirective, TextColorDirective,
  ButtonDirective,
  SpinnerComponent,
  ModalComponent, ModalHeaderComponent, ModalBodyComponent, ModalFooterComponent, ButtonCloseDirective 
} from '@coreui/angular';
import { ToastrService } from 'ngx-toastr'; 
import { ClinicaReadResponse } from '../../../../../models/clinicaModels/clinicaReadResponse';
import { ClinicasService } from '../../../../../services/clinicas/clinicas.service';
import { ClinicaDeleteResponse } from '../../../../../models/clinicaModels/clinicaDeleteResponse';

@Component({
  selector: 'app-clinic-management',
  templateUrl: './clinic-management.component.html',
  standalone: true,
  imports: [
    CommonModule, RouterLink,
    CardBodyComponent, CardComponent, CardHeaderComponent, ColComponent,
    RowComponent, TableDirective, TableColorDirective, TableActiveDirective,
    BorderDirective, AlignDirective, TextColorDirective,
    ButtonDirective,
    SpinnerComponent,
    ModalComponent, ModalHeaderComponent, ModalBodyComponent, ModalFooterComponent, ButtonCloseDirective
  ]
})
export class ClinicManagementComponent implements OnInit {

  public clinicas: ClinicaReadResponse[] = []; 
  public isLoading = true; 
  public errorMessage = ''; 
  public isDeleteModalVisible = false;
  public clinicaIdToDelete: number | null = null;
  public clinicaNomeToDelete: string = '';

  constructor(
      private clinicasService: ClinicasService, 
      private router: Router,
      private toastr: ToastrService 
  ) {} 

  ngOnInit(): void {
    const state = history.state as {showSuccessToast?: boolean, message?: string} | null; 
    if (state?.showSuccessToast && state?.message) {
      this.toastr.success(state.message, 'Sucesso!');
      history.replaceState({...history.state, showSuccessToast: undefined, message: undefined }, '');
    }
    
    this.listarClinicas(); 
  }

  listarClinicas(): void {
    this.isLoading = true;
    this.errorMessage = '';
    this.clinicasService.listarClinicas().subscribe({
      next: (data) => {
        this.clinicas = data;
        this.isLoading = false;
        console.log('Clínicas carregadas:', this.clinicas);
      },
      error: (err) => {
        console.error('Erro ao carregar clínicas:', err);
        this.errorMessage = 'Falha ao carregar a lista de clínicas.';
        this.isLoading = false;
      }
    });
  }


  formatHorario(inicio: string | null | undefined, fim: string | null | undefined): string { 
     if (inicio == null || fim == null) { 
       return '-';
     }
     try {
       const inicioFmt = inicio.substring(0, 5);
       const fimFmt = fim.substring(0, 5);
       return `${inicioFmt} - ${fimFmt}`; 
     } catch (e) {
         console.error("Erro ao formatar horário:", inicio, fim, e);
         return '-'; 
     }
  }
  
  goToCreateClinic(): void {
    this.router.navigate(['/admin/cadastrar-clinica']); 
  }

  openDeleteConfirmationModal(id: number, nome: string): void {
    this.clinicaIdToDelete = id;       
    this.clinicaNomeToDelete = nome; 
    this.isDeleteModalVisible = true; 
  }

  closeDeleteModal(): void {
    this.isDeleteModalVisible = false;
    this.clinicaIdToDelete = null;      
    this.clinicaNomeToDelete = '';     
  }

  confirmDelete(): void {
    if (this.clinicaIdToDelete === null) {
      console.error('ID da clínica para deletar é nulo.');
      this.closeDeleteModal();
      return; 
    }

    this.errorMessage = ''; 

    this.clinicasService.excluirClinica(this.clinicaIdToDelete).subscribe({
      next: (response: ClinicaDeleteResponse) => { 
        console.log(`Clínica ${this.clinicaIdToDelete} excluída. Resposta da API:`, response);
        this.toastr.success(response.mensagemDeResposta || 'Clínica excluída com sucesso!', 'Sucesso!');
        this.listarClinicas(); 
        this.closeDeleteModal(); 
      },
      error: (erro) => {
        console.error('Erro ao excluir clínica:', erro);
        const errorMsg = erro.error?.message || erro.error || 'Falha ao excluir a clínica.';
        this.toastr.error(errorMsg, 'Erro!');
        this.closeDeleteModal(); 
      }
    });
  }
}