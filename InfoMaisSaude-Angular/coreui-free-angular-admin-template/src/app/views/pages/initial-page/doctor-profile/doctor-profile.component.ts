import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms'; 
import { ButtonDirective, CardBodyComponent, CardComponent, CardHeaderComponent, ColComponent, FormControlDirective, FormDirective, FormLabelDirective, RowComponent } from '@coreui/angular';
import { PerfilMedicoService } from '../../../../services/perfil/medico/perfil-medico.service';
import { PerfilMedico } from '../../../../models/perfilModels/perfilMedicoModels/perfilMedico';
import { SolicitacoesService } from '../../../../services/solicitacoes/solicitacoes.service';
import { ToastrService } from 'ngx-toastr';
import { Router } from '@angular/router';
@Component({
  selector: 'app-doctor-profile',
  standalone: true,
  imports: [
    ReactiveFormsModule, 
    CardComponent,
    CardBodyComponent,
    CardHeaderComponent,
    ColComponent,
    FormDirective,
    FormControlDirective,
    FormLabelDirective,
    RowComponent,
    ButtonDirective
  ],
  templateUrl: './doctor-profile.component.html',
  styleUrl: './doctor-profile.component.scss'
})
export class DoctorProfileComponent implements OnInit {

  medicoForm: FormGroup; 

  constructor(
    private perfilService: PerfilMedicoService,
    private fb: FormBuilder,
    private router: Router,
    private solicitacoesService: SolicitacoesService,
    private toastr: ToastrService
  ) {
    this.medicoForm = this.fb.group({
      nome: [''],
      especializacao: [''],
      telefone: [''],
      login: ['']
    });
  }

  ngOnInit(): void {
    this.resgatarPerfilMedico();
  }

  resgatarPerfilMedico(): void {
    this.perfilService.resgatarPerfilMedico().subscribe({
      next: (data: PerfilMedico) => {
        this.medicoForm.patchValue({
          nome: data.nome,
          especializacao: data.especializacao,
          telefone: data.telefone,
          login: data.login
        });
      },
      error: (err) => {
        console.error('Erro ao resgatar o perfil do médico:', err);
      }
    });
  }

  solicitarAlteracaoDePerfil(): void {
    if (this.medicoForm.valid) {
      const payload = this.medicoForm.value;
      console.log('Payload para solicitação de alteração de perfil:', payload);
      this.solicitacoesService.solicitarAlteracaoDePerfil(payload).subscribe({
        next: (response) => {
          console.log('Solicitação de alteração de perfil enviada com sucesso:', response);
          this.toastr.success('Solicitação de alteração de perfil enviada com sucesso!', 'Sucesso');
          this.medicoForm.markAsPristine();
          this.router.navigate(['/medicos/solicitacoes']);
        },
        error: (err) => {
          console.error('Erro ao enviar a solicitação de alteração de perfil:', err);
          this.toastr.error('Erro ao enviar a solicitação de alteração de perfil.', 'Erro');
        }
      });
    }
  }
}