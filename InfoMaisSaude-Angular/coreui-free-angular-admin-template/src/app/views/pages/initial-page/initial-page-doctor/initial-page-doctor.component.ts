import { Component, OnInit } from '@angular/core';
import { CommonModule } from "@angular/common";
import { Router, RouterLink } from '@angular/router';
import { ButtonDirective, CardBodyComponent, CardComponent, CardImgDirective, CardTextDirective, CardTitleDirective, ColComponent, RowComponent } from '@coreui/angular';
import { MedicosService } from 'src/app/services/medicos/medicos.service';

@Component({
  selector: 'app-initial-page-doctor',
  imports: [CommonModule,
    RouterLink,
    RowComponent,
    ColComponent,
    CardComponent,
    CardBodyComponent,
    CardImgDirective,
    CardTitleDirective,
    CardTextDirective,
    ButtonDirective],
  templateUrl: './initial-page-doctor.component.html',
  styleUrl: './initial-page-doctor.component.scss'
})
export class InitialPageDoctorComponent implements OnInit{
  
  usuarioId: number | null = null;
  medicoId: number | null = null;
  constructor(private router: Router, private medicosService: MedicosService) { }

  ngOnInit(): void {
   this.usuarioId = this.medicosService.pegarIdDoMedicoLogado();
   console.log("Usuario ID do médico logado:", this.usuarioId);
 }
  
  resgatandoMedico(): void {
  if(this.usuarioId != null){
  this.medicosService.pegarMedicoPorUsuarioId(this.usuarioId).subscribe({
      next: (medico: any) => {
        this.medicoId = medico.id;
        console.log("Medico ID resgatado:", this.medicoId);
        this.router.navigate(['medicos/agenda/', this.medicoId]);

      },
      error: (err: any) => {
        console.error('Erro ao listar médicos:', err);
      }
    });
  }
  }
  goToDoctorSchedule(): void {
    this.resgatandoMedico();
  }

  goToMyRequests(): void {
    this.router.navigate(['medicos/solicitacoes']);
  }
  goToAppointments(): void {
    this.router.navigate(['appointments']);
  }

}
