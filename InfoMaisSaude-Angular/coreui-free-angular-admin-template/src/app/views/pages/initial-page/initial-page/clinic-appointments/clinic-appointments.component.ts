import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { BadgeModule, ButtonModule, CardModule, FormModule, GridModule, ModalModule, SpinnerModule, TableModule } from '@coreui/angular';
import { ConsultasPorClinicaReadResponse } from '../../../../../models/consultaModels/consultasPorClinica.model';
import { ClinicasService } from '../../../../../services/clinicas/clinicas.service';
import { ToastrService } from 'ngx-toastr';
import { IconModule } from '@coreui/icons-angular'; 
import { MedicosService } from '../../../../../services/medicos/medicos.service';
import { MedicoNomeReadResponse } from '../../../../../models/medicoModels/medicoNomeReadResponse';
import { Consulta } from 'src/app/models/consultaModels/consulta.model';
import { AgendamentoService } from 'src/app/services/agendamento/agendamento.service';

@Component({
  selector: 'app-clinic-appointments',
  standalone: true, 
  imports: [ 
    CommonModule, 
    FormsModule, 
    CardModule, 
    TableModule, 
    BadgeModule, 
    GridModule, 
    SpinnerModule, 
    FormModule, 
    ButtonModule,
    IconModule ,
    ModalModule
  ],
  templateUrl: './clinic-appointments.component.html',
  styleUrl: './clinic-appointments.component.scss'
})
export class ClinicAppointmentsComponent implements OnInit {

  listaConsultasPorClinica: ConsultasPorClinicaReadResponse[] = [];
  loading = false;
  listaMedicos: MedicoNomeReadResponse[] = []; 
  dataFiltro: string = new Date().toISOString().split('T')[0];
  statusFiltro: string = ""; 
  medicoFiltro: string = "";
  ordemHorario: 'asc' | 'desc' = 'asc';

  modalCancelamentoVisible = false;
  consultaParaCancelar: any = null;
  motivoCancelamento: string = '';
  loadingCancelamento = false;



  
  constructor(
    private clinicaService: ClinicasService, 
    private medicosService: MedicosService,
    private toastr: ToastrService,
    private agendamentoService: AgendamentoService
  ) {} 

  ngOnInit(): void {
  const idDaClinica = localStorage.getItem("idDaClinica");
  console.log("Id da clinica", idDaClinica);
     if(idDaClinica) {
        this.filtrarNoBackend(); 
        this.carregarMedicos(idDaClinica); 
     }  
  }

  carregarMedicos(id: any) {
   this.medicosService.pegarMedicosNome(id).subscribe({
    next:  (data) => this.listaMedicos = Array.isArray(data) ? data : [data],
    error: () => this.toastr.error('Erro ao carregar médicos.')
   })
  }

  filtrarNoBackend() {
     const idDaClinica = localStorage.getItem("idDaClinica");
     this.loading = true;
     this.clinicaService.listarConsultasPorClinica(idDaClinica, this.dataFiltro).subscribe({
       next: (dados) => {
         this.listaConsultasPorClinica = Array.isArray(dados) ? dados : [dados];
         console.log(this.listaConsultasPorClinica);
         this.loading = false;
       },
       error: (err) => {
         this.toastr.error('Erro ao carregar a agenda.');
         this.loading = false;
       }
     });
  }

  get consultasFiltradas() {
    let resultado = this.listaConsultasPorClinica.filter(c => {
        const matchStatus = this.statusFiltro ? c.status === this.statusFiltro : true;
        const matchMedico = this.medicoFiltro ? c.nomeMedico === this.medicoFiltro : true;
        
        return matchStatus && matchMedico;
    });

    return resultado.sort((a, b) => {
        if (this.ordemHorario === 'asc') {
            return a.horario.localeCompare(b.horario);
        } else {
            return b.horario.localeCompare(a.horario);
        }
    });
  }

  alternarOrdem() {
      this.ordemHorario = this.ordemHorario === 'asc' ? 'desc' : 'asc';
  }

  iniciarCancelamento(consulta: any) {
    this.consultaParaCancelar = consulta;
    this.motivoCancelamento = '';
    this.modalCancelamentoVisible = true;
  }

  confirmarCancelamento() {
    if (!this.consultaParaCancelar) return;
    
    if (!this.motivoCancelamento.trim()) {
      this.toastr.warning('Por favor, informe o motivo do cancelamento.');
      return;
    }

    this.loadingCancelamento = true;

    this.agendamentoService.cancelarConsulta(this.consultaParaCancelar.id, this.motivoCancelamento)
      .subscribe({
        next: () => {
          this.toastr.success('Consulta cancelada pela clínica.');
          this.modalCancelamentoVisible = false;
          this.loadingCancelamento = false;
          this.filtrarNoBackend(); // Recarrega a tabela
        },
        error: (err) => {
          console.error(err);
          this.toastr.error('Erro ao cancelar consulta.');
          this.loadingCancelamento = false;
        }
      });
  }



  getBadgeColor(status: string): string {
    switch (status) {
      case 'AGENDADA': return 'success';
      case 'CONFIRMADA': return 'info';
      case 'REALIZADA': return 'dark';
      case 'CANCELADA': return 'danger'; 
      case 'CANCELADA_PELO_PACIENTE': return 'warning';
      case 'CANCELADA_PELO_MEDICO': return 'danger';
      case 'CANCELADA_PELA_CLINICA': return 'danger'; 
      case 'PENDENTE': return 'warning';
      default: return 'secondary';
    }
  }

  verDetalhes(motivo: string): void {
    alert(motivo || 'Sem queixa registrada.');
  }


}