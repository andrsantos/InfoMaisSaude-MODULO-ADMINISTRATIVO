import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { MedicoReadResponse } from '../../../../../models/medicoModels/medicoReadResponse';
import { MedicosService } from '../../../../../services/medicos/medicos.service';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ButtonModule, FormModule, GridModule, ModalModule } from '@coreui/angular';
import { AuthService, DecodedToken } from '../../../../../services/auth/auth.service';

@Component({
  selector: 'app-doctors-list',
  imports: [
    CommonModule,
    ReactiveFormsModule,
    ModalModule,         
    ButtonModule,
    FormModule,
    GridModule
  ],
  templateUrl: './doctors-list.component.html',
  styleUrl: './doctors-list.component.scss'
})


export class DoctorsListComponent implements OnInit {

 
  listaMedicos: MedicoReadResponse[] = [];
  editForm: FormGroup;
  visibleEditModal = false;
  medicoSelecionadoId: number | null = null;
  idDaClinica: any;
 

constructor(
    private router: Router, 
    private toastr: ToastrService, 
    private medicoService: MedicosService,
    private fb: FormBuilder
  ) {
    this.editForm = this.fb.group({
      nome: ['', Validators.required],
      especializacao: ['', Validators.required],
      telefone: ['', [Validators.required, Validators.pattern(/^\d+$/)]]
    });
  }
  ngOnInit(): void {

   this.idDaClinica = localStorage.getItem("idDaClinica");

    const state = history.state as {
      showSuccessToast?: boolean;
      message?: string;
    } | null;

    console.log('State lido do history:', state);

    if (state?.showSuccessToast && state?.message) {
      this.toastr.success(state.message, 'Sucesso!');
      history.replaceState(
        { ...history.state, showSuccessToast: undefined, message: undefined },
        ''
      );
    } else {
      console.log('Nenhum state de toast encontrado no history.');
    }
    this.listarMedicos();
  }

  listarMedicos():void {
    this.medicoService.listarMedicosPorClinica(this.idDaClinica).subscribe({
      next: (medicos) => {
        this.listaMedicos = medicos;
        console.log("Lista de médicos:", this.listaMedicos);

      },
      error: (err) => {
        console.error('Erro ao listar médicos:', err);
        this.toastr.error('Erro ao listar médicos.', 'Erro');
      }
    });
  }


  verAgenda(id: number) {
  this.router.navigate(['/medicos/agenda', id]);
 }

  deletarMedico(id: number) {
    const confirmacao = window.confirm('Tem certeza que deseja excluir este médico? Essa ação não pode ser desfeita.');

    if (confirmacao) {
      this.medicoService.excluirMedico(id).subscribe({
        next: () => {
          this.toastr.success('Médico excluído com sucesso!');
          this.listarMedicos(); 
        },
        error: (err) => {
          console.error(err);
          this.toastr.error('Erro ao excluir médico.', 'Erro');
        }
      });
    }
  }

  editarMedico(id: number) {
    const medico = this.listaMedicos.find(m => m.id === id);

    if (medico) {
      this.medicoSelecionadoId = id;
      
      this.editForm.patchValue({
        nome: medico.nome,
        especializacao: medico.especializacao,
        telefone: medico.telefone
      });

      this.visibleEditModal = true;
    }
  }

  salvarEdicao() {
    if (this.editForm.valid && this.medicoSelecionadoId) {
      const dadosAtualizados = {
        nome: this.editForm.get('nome')?.value,
        especializacao: this.editForm.get('especializacao')?.value,
        telefone: this.editForm.get('telefone')?.value,
        agenda: this.listaMedicos.find(m => m.id === this.medicoSelecionadoId)?.agenda || []
      };

      this.medicoService.atualizarMedico(this.medicoSelecionadoId, dadosAtualizados).subscribe({
        next: () => {
          this.toastr.success('Dados do médico atualizados!');
          this.visibleEditModal = false; 
          this.listarMedicos(); 
        },
        error: (err) => {
          console.error(err);
          this.toastr.error('Erro ao atualizar médico.', 'Erro');
        }
      });
    } else {
      this.editForm.markAllAsTouched(); 
  }
}
  handleModalChange(event: boolean) {
    this.visibleEditModal = event;
  }

novoMedico(){
  this.router.navigate(['/register-doctor']);
}

}