import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { 
  CardModule, TableModule, BadgeModule, GridModule, SpinnerModule, FormModule, ButtonModule 
} from '@coreui/angular';
import { ToastrService } from 'ngx-toastr';
import { Consulta } from '../../../../models/consultaModels/consulta.model';
import { AgendamentoService } from '../../../../services/agendamento/agendamento.service';

@Component({
  selector: 'app-appointments',
  standalone: true,
  imports: [
    CommonModule, FormsModule, CardModule, TableModule, BadgeModule, 
    GridModule, SpinnerModule, FormModule, ButtonModule
  ],
  templateUrl: './appointments.component.html',
  styleUrl: './appointments.component.scss'
})
export class AppointmentsComponent implements OnInit {

  listaConsultas: Consulta[] = [];
  loading = false;
  
  dataFiltro: string = new Date().toISOString().split('T')[0];

  constructor(
    private agendamentoService: AgendamentoService,
    private toastr: ToastrService
  ) {}

  ngOnInit(): void {
    this.carregarConsultas();
  }

  carregarConsultas() {
    this.loading = true;
    this.agendamentoService.listarConsultas(this.dataFiltro).subscribe({
      next: (dados) => {
        this.listaConsultas = dados;
        this.loading = false;
      },
      error: (err) => {
        console.error(err);
        this.toastr.error('Erro ao carregar a agenda.');
        this.loading = false;
      }
    });
  }

  aoMudarData() {
    this.carregarConsultas();
  }

  getStatusColor(status: string): string {
    switch(status) {
      case 'AGENDADA': return 'primary';
      case 'CONFIRMADA': return 'success';
      case 'REALIZADA': return 'dark';
      case 'CANCELADA_PELO_PACIENTE': return 'warning';
      case 'CANCELADA_PELO_MEDICO': return 'danger';
      default: return 'secondary';
    }
  }
}