import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ToastrService } from 'ngx-toastr';
import { ModalModule, ButtonModule } from '@coreui/angular';
import { MedicoReadResponse } from '../../../../../../models/medicoModels/medicoReadResponse';
import { MedicosService } from '../../../../../../services/medicos/medicos.service';
import { AgendaItem } from '../../../../../../models/medicoModels/agendaItem';


@Component({
  selector: 'app-doctor-schedule',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, ModalModule, ButtonModule], // Adicione ReactiveFormsModule
  templateUrl: './doctor-schedule.component.html',
  styleUrl: './doctor-schedule.component.scss'
})
export class DoctorScheduleComponent implements OnInit {
  
  medico: MedicoReadResponse | null = null;
  medicoBackup: MedicoReadResponse | null = null; 
  isEditing = false;
  modalVisible = false;
  diaSelecionadoParaAdicao: number | null = null;
  formHorario: FormGroup;

  diasDaSemana = [
    { id: 1, label: 'Segunda-feira' },
    { id: 2, label: 'Terça-feira' },
    { id: 3, label: 'Quarta-feira' },
    { id: 4, label: 'Quinta-feira' },
    { id: 5, label: 'Sexta-feira' },
    { id: 6, label: 'Sábado' },
    { id: 7, label: 'Domingo' }
  ];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private medicosService: MedicosService,
    private toastr: ToastrService,
    private fb: FormBuilder
  ) {
    this.formHorario = this.fb.group({
      inicio: ['', Validators.required],
      fim: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) this.carregarMedico(Number(id));
  }

  carregarMedico(id: number) {
    this.medicosService.pegarMedico(id).subscribe({
      next: (dados) => {
        this.medico = dados;
      },
      error: () => this.router.navigate(['/medicos/listar'])
    });
  }

  getHorariosDoDia(diaId: number): AgendaItem[] {
    if (!this.medico) return [];
    return this.medico.agenda
      .filter(a => a.diaSemana === diaId)
      .sort((a, b) => a.horarioInicio.localeCompare(b.horarioInicio));
  }


  toggleEditMode() {
    this.medicoBackup = JSON.parse(JSON.stringify(this.medico));
    this.isEditing = true;
  }

  cancelarEdicao() {
    this.medico = this.medicoBackup; 
    this.isEditing = false;
    this.medicoBackup = null;
  }

  removerHorario(item: AgendaItem) {
    if (this.medico) {
      this.medico.agenda = this.medico.agenda.filter(a => a !== item);
    }
  }

  abrirModalAdicionar(diaId: number) {
    this.diaSelecionadoParaAdicao = diaId;
    this.formHorario.reset();
    this.modalVisible = true;
  }

  confirmarAdicao() {
    if (this.formHorario.valid && this.medico && this.diaSelecionadoParaAdicao) {
      const { inicio, fim } = this.formHorario.value;

      if (inicio >= fim) {
        this.toastr.warning('O horário final deve ser maior que o inicial');
        return;
      }

      const novoItem: AgendaItem = {
        id: 0, 
        diaSemana: this.diaSelecionadoParaAdicao,
        horarioInicio: inicio,
        horarioFim: fim
      };

      this.medico.agenda.push(novoItem);
      this.modalVisible = false;
    }
  }

  salvarAlteracoes() {
    if (!this.medico) return;

    const payload = {
      nome: this.medico.nome,
      especializacao: this.medico.especializacao,
      telefone: this.medico.telefone,
      agenda: this.medico.agenda.map(item => ({
        diaSemana: item.diaSemana,
        horarioInicio: item.horarioInicio,
        horarioFim: item.horarioFim
      }))
    };

    this.medicosService.atualizarMedico(this.medico.id, payload).subscribe({
      next: () => {
        this.toastr.success('Agenda atualizada com sucesso!');
        this.isEditing = false;
        this.carregarMedico(this.medico!.id); 
      },
      error: (err) => {
        console.error(err);
        this.toastr.error('Erro ao salvar alterações.');
      }
    });
  }

  getNomeDia(diaId: number | null): string {
    if (!diaId) return '';
    return this.diasDaSemana.find(d => d.id === diaId)?.label || '';
  }
  
  voltar() {
    this.router.navigate(['/doctors-list']);
  }
}