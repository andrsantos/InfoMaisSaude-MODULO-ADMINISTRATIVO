import { Component } from "@angular/core";
import { CommonModule } from "@angular/common";
import {
  ReactiveFormsModule,
  FormBuilder,
  FormGroup,
  Validators,
} from "@angular/forms";
import { Router } from "@angular/router";
import {
  CardComponent, CardBodyComponent, CardHeaderComponent, ColComponent,
  FormDirective, FormControlDirective, FormLabelDirective, RowComponent,
  ButtonDirective, FormSelectDirective, ModalComponent, ModalHeaderComponent,
  ModalBodyComponent, ModalFooterComponent, TableDirective,
} from "@coreui/angular";
import { MedicosService } from "../../../../../services/medicos/medicos.service";
import { ToastrService } from "ngx-toastr";

interface HorarioFrontend {
  diaDaSemana: string; 
  horarioInicio: string;
  horarioFim: string;
}

const MAPA_DIAS_SEMANA: { [key: string]: number } = {
  "SEGUNDA": 1,
  "TERCA": 2,
  "QUARTA": 3,
  "QUINTA": 4,
  "SEXTA": 5,
  "SABADO": 6,
  "DOMINGO": 7
};

@Component({
  selector: "app-register-doctor",
  templateUrl: "./register-doctor.component.html",
  standalone: true,
  imports: [
    CommonModule, ReactiveFormsModule, CardComponent, CardHeaderComponent,
    CardBodyComponent, RowComponent, ColComponent, FormDirective,
    FormLabelDirective, FormControlDirective, ButtonDirective,
    FormSelectDirective, ModalComponent, ModalHeaderComponent,
    ModalBodyComponent, ModalFooterComponent, TableDirective,
  ],
})
export class RegisterDoctorComponent {
  medicoForm: FormGroup;
  horarioForm: FormGroup;

  horarios: HorarioFrontend[] = []; 
  isHorarioModalVisible = false;

  diasDaSemana = Object.keys(MAPA_DIAS_SEMANA); 

  errorMessage: string = "";
  successMessage: string = "";

  constructor(
    private fb: FormBuilder, 
    private router: Router, 
    private medicosService: MedicosService,
    private toastr: ToastrService
  ) {
    this.medicoForm = this.fb.group({
      nome: ["", Validators.required],
      especializacao: ["", Validators.required],
      telefone: ["", [Validators.required, Validators.pattern(/^\d+$/)]], 
      login: ["", Validators.required],
      senha: ["", Validators.required]
    });

    this.horarioForm = this.fb.group({
      diaDaSemana: ["", Validators.required],
      horarioInicio: ["", Validators.required],
      horarioFim: ["", Validators.required],
    });
  }

  openHorarioModal(): void {
    this.horarioForm.reset();
    this.horarioForm.patchValue({ diaDaSemana: "" }); 
    this.isHorarioModalVisible = true;
  }

  closeHorarioModal(): void {
    this.isHorarioModalVisible = false;
  }

  adicionarHorario(): void {
    if (this.horarioForm.valid) {
      const novoHorario: HorarioFrontend = this.horarioForm.value;
      
      this.horarios.push(novoHorario);
      this.closeHorarioModal();
    } else {
      this.horarioForm.markAllAsTouched();
    }
  }

  get maxRows(): number {
    if (this.horarios.length === 0) return 1;
    const counts: { [key: string]: number } = {};
    this.diasDaSemana.forEach((dia) => (counts[dia] = 0));
    this.horarios.forEach((h) => counts[h.diaDaSemana]++);
    return Math.max(...Object.values(counts), 1);
  }

  get rowsArray(): number[] {
    return Array(this.maxRows).fill(0).map((x, i) => i);
  }

  getHorarioParaCelula(rowIndex: number, dia: string): string {
    const horariosDoDia = this.horarios.filter((h) => h.diaDaSemana === dia);
    const horario = horariosDoDia[rowIndex];
    return horario ? `${horario.horarioInicio} - ${horario.horarioFim}` : "";
  }

  cadastrarMedico(): void {
    this.errorMessage = "";
    this.successMessage = "";
    const clinica_id = localStorage.getItem("idDaClinica");

    if (this.medicoForm.valid) {
      const agendaBackend = this.horarios.map(item => ({
        diaSemana: MAPA_DIAS_SEMANA[item.diaDaSemana], 
        horarioInicio: item.horarioInicio,
        horarioFim: item.horarioFim
      }));

      const payload = {
        ...this.medicoForm.value,
        agenda: agendaBackend,
        clinica_id
      };

      console.log("Enviando para o backend:", payload);

      this.medicosService.cadastrarMedico(payload).subscribe({
        next: (response) => {
          this.successMessage = `Médico ${response.nomeDoMedicoCriado} cadastrado com sucesso!`;
          this.medicoForm.reset();
          this.horarios = [];
          this.router.navigate(["/doctors-list"], {
            state: {
              showSuccessToast: true,
              message: response.mensagemDeResposta,
            },
          });         
        },
        error: (err) => {
          console.error("Erro ao cadastrar:", err);
          this.errorMessage = err.error?.message || "Erro ao cadastrar médico. Verifique os dados.";
        }
      });

    } else {
      this.errorMessage = "Por favor, preencha todos os campos obrigatórios.";
      this.medicoForm.markAllAsTouched();
    }
  }

  getHorarioItem(rowIndex: number, dia: string): HorarioFrontend | undefined {
    const horariosDoDia = this.horarios.filter((h) => h.diaDaSemana === dia);
    return horariosDoDia[rowIndex];
  }

  removerHorario(item: HorarioFrontend): void {
    const index = this.horarios.indexOf(item);
    if (index > -1) {
      this.horarios.splice(index, 1);
    }
  }


}