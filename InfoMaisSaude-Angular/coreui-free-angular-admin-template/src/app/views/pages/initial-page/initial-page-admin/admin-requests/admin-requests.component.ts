import { Component, OnInit } from '@angular/core';
import { SolicitacoesService } from '../../../../../services/solicitacoes/solicitacoes.service';
import { ToastrService } from 'ngx-toastr';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { BadgeModule, ButtonModule, CardModule, FormModule, GridModule, ModalModule, SpinnerModule, TableModule } from '@coreui/angular';

@Component({
  selector: 'app-admin-requests',
  standalone: true,
  imports: [ CommonModule, 
    FormsModule, 
    CardModule, 
    TableModule, 
    BadgeModule, 
    ButtonModule, 
    ModalModule, 
    FormModule, 
    GridModule, 
    SpinnerModule],
  templateUrl: './admin-requests.component.html',
  styleUrl: './admin-requests.component.scss'
})
export class AdminRequestsComponent implements OnInit {

  listaSolicitacoes: any[] = [];
  loading = false;

  modalDetalhesVisible = false;
  modalRejeicaoVisible = false;

  solicitacaoSelecionada: any = null;
  dadosNovosParseados: any = null; 
  motivoRejeicao: string = '';

  constructor(
    private solicitacoesService: SolicitacoesService,
    private toastr: ToastrService
  ) {}

  ngOnInit(): void {
    this.carregarPendencias();
  }

  carregarPendencias() {
    this.loading = true;
    this.solicitacoesService.listarPendentes().subscribe({
      next: (dados) => {
        this.listaSolicitacoes = dados.filter((s: any) => s.tipo === 'ALTERACAO_DADOS_CLINICA');
        this.loading = false;
      },
      error: (err) => {
        console.error(err);
        this.toastr.error('Erro ao carregar solicitações.');
        this.loading = false;
      }
    });
  }


  verDetalhes(solicitacao: any) {
    this.solicitacaoSelecionada = solicitacao;
    try {
      this.dadosNovosParseados = JSON.parse(solicitacao.dadosNovos);
    } catch (e) {
      this.dadosNovosParseados = {};
      console.error("Erro ao fazer parse dos dados novos", e);
    }
    this.modalDetalhesVisible = true;
  }

  abrirModalRejeicao(solicitacao: any) {
    this.solicitacaoSelecionada = solicitacao;
    this.motivoRejeicao = ''; 
    this.modalRejeicaoVisible = true;
  }


  aprovar() {
    if (!this.solicitacaoSelecionada) return;

    if(confirm('Tem certeza que deseja aprovar e atualizar os dados da clínica?')) {
        this.solicitacoesService.aprovarSolicitacao(this.solicitacaoSelecionada.id).subscribe({
        next: () => {
            this.toastr.success('Solicitação aprovada e dados atualizados!');
            this.modalDetalhesVisible = false;
            this.carregarPendencias(); 
        },
        error: () => this.toastr.error('Erro ao aprovar solicitação.')
        });
    }
  }

  confirmarRejeicao() {
    if (!this.motivoRejeicao.trim()) {
      this.toastr.warning('Por favor, informe o motivo da rejeição.');
      return;
    }

    this.solicitacoesService.rejeitarSolicitacao(this.solicitacaoSelecionada.id, this.motivoRejeicao)
      .subscribe({
        next: () => {
          this.toastr.info('Solicitação rejeitada.');
          this.modalRejeicaoVisible = false;
          this.carregarPendencias();
        },
        error: () => this.toastr.error('Erro ao rejeitar solicitação.')
      });
  }

}
