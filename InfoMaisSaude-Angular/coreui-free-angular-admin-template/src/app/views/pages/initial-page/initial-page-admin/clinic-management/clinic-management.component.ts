import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common'; 
import { RouterLink } from '@angular/router'; 
import {
  CardBodyComponent, CardComponent, CardHeaderComponent, ColComponent,
  RowComponent, TableDirective, TableColorDirective, TableActiveDirective,
  BorderDirective, AlignDirective, TextColorDirective,
  ButtonDirective
} from '@coreui/angular';

enum Especializacao {
  MEDICA = 'MEDICA', 
  ODONTOLOGICA = 'ODONTOLOGICA' ,
  AMBAS = 'ODONTOLOGICA/MEDICA'
}

interface ClinicaDisplay {
  id: number;
  nome: string;
  endereco: string;
  email: string;
  telefone: string;
  especializacoes: Especializacao[]; 
  horarioFuncionamentoInicio: string; 
  horarioFuncionamentoFinal: string; 
  site?: string; 
}

@Component({
  selector: 'app-clinic-management',
  templateUrl: './clinic-management.component.html',
  standalone: true,
  imports: [
    CommonModule, RouterLink,
    CardBodyComponent, CardComponent, CardHeaderComponent, ColComponent,
    RowComponent, TableDirective, TableColorDirective, TableActiveDirective,
    BorderDirective, AlignDirective, TextColorDirective,
    ButtonDirective
  ]
})
export class ClinicManagementComponent implements OnInit {

  public clinicas: ClinicaDisplay[] = [];

  constructor() { } 

  ngOnInit(): void {
    this.clinicas = [
      { 
        id: 1, 
        nome: 'Clínica Sorriso Feliz', 
        endereco: 'Rua das Flores, 123, Centro', 
        email: 'contato@sorriso.com', 
        telefone: '(11) 98765-4321', 
        especializacoes: [Especializacao.ODONTOLOGICA], 
        horarioFuncionamentoInicio: '09:00', 
        horarioFuncionamentoFinal: '19:00',
        site: 'www.sorriso.com'
      },
      { 
        id: 2, 
        nome: 'Centro Médico Viver Bem', 
        endereco: 'Av. Principal, 456, Bairro Norte', 
        email: 'contato@viverbem.med', 
        telefone: '(21) 12345-6789', 
        especializacoes: [Especializacao.MEDICA], 
        horarioFuncionamentoInicio: '08:00', 
        horarioFuncionamentoFinal: '18:00' 
      },
      { 
        id: 3, 
        nome: 'Saúde Integrada Mais', 
        endereco: 'Praça da Saúde, 789, Sul', 
        email: 'contato@saudemais.com', 
        telefone: '(31) 55555-5555', 
        especializacoes: [Especializacao.MEDICA, Especializacao.ODONTOLOGICA], 
        horarioFuncionamentoInicio: '07:30', 
        horarioFuncionamentoFinal: '17:30',
        site: 'www.saudemais.com'
      }
    ];
  }

  formatEspecializacoes(especializacoes: Especializacao[]): string {
    return especializacoes.join('/'); 
  }

  formatHorario(inicio: string, fim: string): string {
    return `${inicio} - ${fim}`; 
  }


}