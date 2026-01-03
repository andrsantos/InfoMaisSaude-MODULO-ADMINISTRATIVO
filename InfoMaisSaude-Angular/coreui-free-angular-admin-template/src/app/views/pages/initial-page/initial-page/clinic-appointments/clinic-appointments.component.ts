import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { BadgeModule, ButtonModule, CardModule, FormModule, GridModule, SpinnerModule, TableModule } from '@coreui/angular';
import { ConsultasPorClinicaReadResponse } from '../../../../../models/consultaModels/consultasPorClinica.model';
import { ClinicasService } from '../../../../../services/clinicas/clinicas.service';
import { ToastrService } from 'ngx-toastr';
import { IconModule } from '@coreui/icons-angular'; 

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
    IconModule 
  ],
  templateUrl: './clinic-appointments.component.html',
  styleUrl: './clinic-appointments.component.scss'
})
export class ClinicAppointmentsComponent implements OnInit {

  listaConsultasPorClinica: ConsultasPorClinicaReadResponse[] = [];
  loading = false;


  dataFiltro: string = new Date().toISOString().split('T')[0];
  statusFiltro: string = ""; 
  
  constructor(
    private clinicaService: ClinicasService, 
    private toastr: ToastrService
  ) {} 

  ngOnInit(): void {
     this.filtrarNoBackend();
  }

  filtrarNoBackend() {
     const idDaClinica = localStorage.getItem("idDaClinica");
     if(idDaClinica) {
        this.carregarConsultas(idDaClinica, this.dataFiltro);
     } else {
        this.toastr.error("ID da clínica não encontrado.");
     }
  }

  carregarConsultas(idDaClinica: any, data: string) {
    this.loading = true;
    this.clinicaService.listarConsultasPorClinica(idDaClinica, data).subscribe({
      next: (dados) => {
        this.listaConsultasPorClinica = Array.isArray(dados) ? dados : [dados];
        this.loading = false;
        console.log("Consultas carregadas para " + data, this.listaConsultasPorClinica);
      },
      error: (err) => {
        console.error(err);
        this.toastr.error('Erro ao carregar a agenda.');
        this.loading = false;
      }
    });
  }

 
  get consultasFiltradas() {
    if (!this.statusFiltro) {
        return this.listaConsultasPorClinica;
    }
    return this.listaConsultasPorClinica.filter(c => c.status === this.statusFiltro);
  }


  getBadgeColor(status: string): string {
    switch (status) {
      case 'AGENDADA': return 'success';
      case 'REALIZADA': return 'primary';
      case 'CANCELADA': return 'danger';
      case 'PENDENTE': return 'warning';
      default: return 'secondary';
    }
  }

  verDetalhes(motivo: string): void {
    alert(motivo);
  }
}