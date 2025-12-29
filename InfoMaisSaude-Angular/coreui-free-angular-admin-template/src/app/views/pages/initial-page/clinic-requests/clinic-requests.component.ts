import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms'; 
import { ToastrService } from 'ngx-toastr';
import { 
  CardModule, TableModule, BadgeModule, ButtonModule, ModalModule, 
  FormModule, GridModule, SpinnerModule 
} from '@coreui/angular';
import { Solicitacao } from '../../../../models/solicitacaoModels/solicitacao.model';
import { SolicitacoesService } from '../../../../services/solicitacoes/solicitacoes.service';

@Component({
  selector: 'app-clinic-requests',
  standalone: true,
  imports: [
    CommonModule, FormsModule, CardModule, TableModule, BadgeModule, 
    ButtonModule, ModalModule, FormModule, GridModule, SpinnerModule
  ],
  templateUrl: './clinic-requests.component.html',
  styleUrl: './clinic-requests.component.scss'
})
export class ClinicRequestsComponent implements OnInit {
  
  perfilDecodificado: any = null;
  listaPendentes: Solicitacao[] = [];
  loading = true;
  modalVisible = false;
  solicitacaoSelecionada: Solicitacao | null = null;
  agendaDecodificada: any[] = []; 
  motivoRejeicao = '';
  showRejeicaoInput = false;

  diasMapa: { [key: number]: string } = {
    1: 'Segunda', 2: 'Terça', 3: 'Quarta', 4: 'Quinta', 5: 'Sexta', 6: 'Sábado', 7: 'Domingo'
  };

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
      next: (data) => {
        this.listaPendentes = data;
        this.loading = false;
      },
      error: (err) => {
        console.error(err);
        this.toastr.error('Erro ao carregar solicitações.');
        this.loading = false;
      }
    });
  }

  abrirDetalhes(solicitacao: Solicitacao) {
    this.solicitacaoSelecionada = solicitacao;
    this.showRejeicaoInput = false;
    this.motivoRejeicao = '';
    
    this.agendaDecodificada = [];
    this.perfilDecodificado = null;

    if (solicitacao.tipo === 'ALTERACAO_AGENDA') {
      try {
        this.agendaDecodificada = JSON.parse(solicitacao.dadosNovos);
        this.agendaDecodificada.sort((a: any, b: any) => a.diaSemana - b.diaSemana);
      } catch (e) {
        console.error('Erro ao ler JSON da Agenda', e);
      }
    } 
    else if (solicitacao.tipo === 'ALTERACAO_DADOS_CADASTRAIS') {
      try {
        this.perfilDecodificado = JSON.parse(solicitacao.dadosNovos);
      } catch (e) {
        console.error('Erro ao ler JSON do Perfil', e);
      }
    }
    
    this.modalVisible = true;
  }

  aprovar() {
    if (!this.solicitacaoSelecionada) return;

    if(confirm('Tem certeza que deseja aprovar e aplicar esta alteração imediatamente?')) {
      this.solicitacoesService.aprovarSolicitacao(this.solicitacaoSelecionada.id).subscribe({
        next: () => {
          this.toastr.success('Solicitação aprovada e aplicada!');
          this.modalVisible = false;
          this.carregarPendencias(); 
        },
        error: () => this.toastr.error('Erro ao aprovar.')
      });
    }
  }

  prepararRejeicao() {
    this.showRejeicaoInput = true;
  }

  confirmarRejeicao() {
    if (!this.solicitacaoSelecionada) return;
    if (!this.motivoRejeicao.trim()) {
      this.toastr.warning('Digite o motivo da rejeição.');
      return;
    }

    this.solicitacoesService.rejeitarSolicitacao(this.solicitacaoSelecionada.id, this.motivoRejeicao).subscribe({
      next: () => {
        this.toastr.info('Solicitação rejeitada.');
        this.modalVisible = false;
        this.carregarPendencias();
      },
      error: () => this.toastr.error('Erro ao rejeitar.')
    });
  }

  getNomeDia(num: number) {
    return this.diasMapa[num] || 'Dia ' + num;
  }
}