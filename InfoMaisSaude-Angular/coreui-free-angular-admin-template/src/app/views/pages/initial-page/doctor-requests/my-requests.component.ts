import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms'; // IMPORTANTE
import { 
  CardModule, TableModule, BadgeModule, GridModule, SpinnerModule, 
  FormModule, ButtonModule // Adicione FormModule e ButtonModule
} from '@coreui/angular';
import { Solicitacao } from '../../../../models/solicitacaoModels/solicitacao.model';
import { SolicitacoesService } from '../../../../services/solicitacoes/solicitacoes.service';

@Component({
  selector: 'app-my-requests',
  standalone: true,
  imports: [
    CommonModule, 
    FormsModule, // Adicionado
    CardModule, 
    TableModule, 
    BadgeModule, 
    GridModule, 
    SpinnerModule,
    FormModule, // Adicionado
    ButtonModule // Adicionado
  ],
  templateUrl: './my-requests.component.html',
  styleUrl: './my-requests.component.scss'
})
export class MyRequestsComponent implements OnInit {

  listaSolicitacoes: Solicitacao[] = [];
  loading = true;

  // Variáveis de Filtro
  filtroStatus: string = '';
  filtroTipo: string = '';
  filtroData: string = ''; // Formato YYYY-MM-DD vindo do input date

  // Variável de Ordenação
  ordemData: 'asc' | 'desc' = 'desc'; // Padrão: Mais recentes primeiro

  constructor(private solicitacoesService: SolicitacoesService) {}

  ngOnInit(): void {
    this.carregarPedidos();
  }

  carregarPedidos() {
    this.loading = true;
    this.solicitacoesService.listarMeusPedidos().subscribe({
      next: (data) => {
        this.listaSolicitacoes = data;
        this.loading = false;
      },
      error: (err) => {
        console.error(err);
        this.loading = false;
      }
    });
  }

  // --- LÓGICA DE FILTRAGEM E ORDENAÇÃO ---

  get listaFiltrada() {
    // 1. Cópia da lista original
    let lista = [...this.listaSolicitacoes];

    // 2. Filtro de Status
    if (this.filtroStatus) {
      lista = lista.filter(item => item.status === this.filtroStatus);
    }

    // 3. Filtro de Tipo
    if (this.filtroTipo) {
      lista = lista.filter(item => item.tipo === this.filtroTipo);
    }

    // 4. Filtro de Data
    if (this.filtroData) {
      lista = lista.filter(item => {
        // item.criadoEm geralmente vem como "2023-12-30T10:00:00"
        // Pegamos apenas a parte da data (os primeiros 10 caracteres)
        const dataItem = item.criadoEm.toString().substring(0, 10);
        return dataItem === this.filtroData;
      });
    }

    // 5. Ordenação (Sempre ativa, respeitando a direção escolhida)
    return lista.sort((a, b) => {
      const timeA = new Date(a.criadoEm).getTime();
      const timeB = new Date(b.criadoEm).getTime();

      if (this.ordemData === 'asc') {
        return timeA - timeB; // Mais antigos primeiro
      } else {
        return timeB - timeA; // Mais recentes primeiro
      }
    });
  }

  alternarOrdem() {
    this.ordemData = this.ordemData === 'asc' ? 'desc' : 'asc';
  }

  limparFiltros() {
    this.filtroStatus = '';
    this.filtroTipo = '';
    this.filtroData = '';
    this.ordemData = 'desc'; // Reseta para o padrão
  }

  // --- MÉTODOS AUXILIARES VISUAIS ---

  getBadgeColor(status: string): string {
    switch (status) {
      case 'APROVADO': return 'success';
      case 'REJEITADO': return 'danger';
      case 'PENDENTE': return 'warning';
      default: return 'secondary';
    }
  }

  getTipoLabel(tipo: string): string {
    if (tipo === 'ALTERACAO_AGENDA') return 'Alteração de Agenda';
    if (tipo === 'ALTERACAO_DADOS_CADASTRAIS') return 'Dados Cadastrais';
    return tipo;
  }
}