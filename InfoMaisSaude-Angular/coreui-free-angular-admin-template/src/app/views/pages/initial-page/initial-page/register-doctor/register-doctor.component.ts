import { Component } from '@angular/core';
import { CommonModule } from '@angular/common'; // Para *ngFor, *ngIf, etc.
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms'; // Reactive Forms
import { Router } from '@angular/router'; // Para navegação futura
import {
  CardComponent, CardBodyComponent, CardHeaderComponent, ColComponent,
  FormDirective, FormControlDirective, FormLabelDirective, RowComponent,
  ButtonDirective, FormSelectDirective, // Para selects
  ModalComponent, ModalHeaderComponent, ModalBodyComponent, ModalFooterComponent, // Para a Modal
  TableDirective 
} from '@coreui/angular';


interface Horario {
  diaDaSemana: string; 
  horarioInicio: string; 
  horarioFim: string;    
}

@Component({
  selector: 'app-register-doctor',
  templateUrl: './register-doctor.component.html',
  standalone: true,
  imports: [
    CommonModule, ReactiveFormsModule,
    CardComponent, CardHeaderComponent, CardBodyComponent, RowComponent, ColComponent,
    FormDirective, FormLabelDirective, FormControlDirective, ButtonDirective, FormSelectDirective,
    ModalComponent, ModalHeaderComponent, ModalBodyComponent, ModalFooterComponent,
    TableDirective
  ]
})
export class RegisterDoctorComponent {

  medicoForm: FormGroup;
  horarioForm: FormGroup; 

  horarios: Horario[] = []; 
  isHorarioModalVisible = false; 

  diasDaSemana = ['SEGUNDA', 'TERCA', 'QUARTA', 'QUINTA', 'SEXTA', 'SABADO', 'DOMINGO'];

  errorMessage: string = '';
  successMessage: string = '';

  constructor(
    private fb: FormBuilder,
    private router: Router
  ) {
    this.medicoForm = this.fb.group({
      nome: ['', Validators.required],
      especializacao: ['', Validators.required] 
    });

    this.horarioForm = this.fb.group({
      diaDaSemana: ['', Validators.required],
      horarioInicio: ['', Validators.required],
      horarioFim: ['', Validators.required]
    });
  }

  openHorarioModal(): void {
    this.horarioForm.reset(); 
    this.isHorarioModalVisible = true;
  }

  closeHorarioModal(): void {
    this.isHorarioModalVisible = false;
  }

  adicionarHorario(): void {
    if (this.horarioForm.valid) {
      const novoHorario: Horario = this.horarioForm.value;
      this.horarios.push(novoHorario);
      console.log('Horários atuais:', this.horarios); 
      this.closeHorarioModal();
    } else {
      this.horarioForm.markAllAsTouched();
    }
  }

  get maxRows(): number {
    if (this.horarios.length === 0) return 1; 
    const counts: { [key: string]: number } = {};
    this.diasDaSemana.forEach(dia => counts[dia] = 0);
    this.horarios.forEach(h => counts[h.diaDaSemana]++);
    return Math.max(...Object.values(counts), 1); 
  }

  get rowsArray(): number[] {
    return Array(this.maxRows).fill(0).map((x, i) => i);
  }

  getHorarioParaCelula(rowIndex: number, dia: string): string {
    const horariosDoDia = this.horarios.filter(h => h.diaDaSemana === dia);
    const horario = horariosDoDia[rowIndex]; // Pega o horário correspondente à linha
    return horario ? `${horario.horarioInicio} - ${horario.horarioFim}` : ''; // Retorna formatado ou vazio
  }

  cadastrarMedico(): void {
    this.errorMessage = '';
    this.successMessage = '';

    if (this.medicoForm.valid) {
      const payload = {
        ...this.medicoForm.value,
        agenda: this.horarios 
      };
      console.log('Payload final:', payload);
      this.successMessage = 'Médico pronto para ser cadastrado (ver console)!';

    } else {
      this.errorMessage = 'Preencha os dados do médico.';
      this.medicoForm.markAllAsTouched();
    }
  }
}