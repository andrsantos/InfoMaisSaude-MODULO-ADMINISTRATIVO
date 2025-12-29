import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { 
  CardModule, TableModule, BadgeModule, GridModule, SpinnerModule 
} from '@coreui/angular';
import { Solicitacao } from '../../../../models/solicitacaoModels/solicitacao.model';
import { SolicitacoesService } from '../../../../services/solicitacoes/solicitacoes.service';

@Component({
  selector: 'app-my-requests',
  standalone: true,
  imports: [CommonModule, CardModule, TableModule, BadgeModule, GridModule, SpinnerModule],
  templateUrl: './my-requests.component.html',
  styleUrl: './my-requests.component.scss'
})
export class MyRequestsComponent implements OnInit {

  listaSolicitacoes: Solicitacao[] = [];
  loading = true;

  constructor(private solicitacoesService: SolicitacoesService) {}

  ngOnInit(): void {
    this.carregarPedidos();
  }

  carregarPedidos() {
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