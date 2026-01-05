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
import { AuthService } from '../../../../services/auth/auth.service';
import { MedicosService } from '../../../../services/medicos/medicos.service';
import { MedicoNomeReadResponse } from '../../../../models/medicoModels/medicoNomeReadResponse';

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

  activeTab: 'recebidas' | 'enviadas' = 'recebidas';
  perfilDecodificado: any = null;
  listaPendentes: Solicitacao[] = [];
  loading = true;
  modalVisible = false;
  solicitacaoSelecionada: Solicitacao | null = null;
  agendaDecodificada: any[] = []; 
  motivoRejeicao = '';
  showRejeicaoInput = false;

  todasSolicitacoes: Solicitacao[] = []; 
  listaRecebidas: Solicitacao[] = [];
  listaEnviadas: Solicitacao[] = [];

  userId: number | null = null;

  filtroTipo: string = '';
  filtroStatus: string = '';
  filtroSolicitante: string = ''; 
  ordemData: 'asc' | 'desc' = 'desc'; 
  
  listaMedicos: MedicoNomeReadResponse[] = [];

  diasMapa: { [key: number]: string } = {
    1: 'Segunda', 2: 'Terça', 3: 'Quarta', 4: 'Quinta', 5: 'Sexta', 6: 'Sábado', 7: 'Domingo'
  };

  constructor(
    private solicitacoesService: SolicitacoesService,
    private toastr: ToastrService,
    private authService: AuthService,
    private medicosService: MedicosService 
  ) {}

  ngOnInit(): void {
    const user = this.authService.getCurrentUser(); 
    this.userId = user ? user.id : null;
    
    this.carregarTodos();
    this.carregarMedicos(); 
  }

  carregarTodos() {
    this.loading = true;
    this.solicitacoesService.listarTodos(this.userId).subscribe({
      next: (data) => {
        this.todasSolicitacoes = data;
        this.separarListas(); 
        this.loading = false;
      },
      error: (err) => {
        console.error(err);
        this.toastr.error('Erro ao carregar solicitações.');
        this.loading = false;
      }
    });
  }

  carregarMedicos() {
    const idClinica = localStorage.getItem('idDaClinica');
    if (idClinica) {
      this.medicosService.pegarMedicosNome(idClinica).subscribe({
        next: (dados) => {
           this.listaMedicos = Array.isArray(dados) ? dados : [dados];
        },
        error: () => console.error("Erro ao carregar lista de médicos para filtro")
      });
    }
  }

  separarListas() {
    if (!this.userId) return;

    this.listaRecebidas = this.todasSolicitacoes.filter(s => 
        s.solicitante?.id !== this.userId
    );

    this.listaEnviadas = this.todasSolicitacoes.filter(s => 
        s.solicitante?.id === this.userId
    );
  }

  get listaFiltrada() {
    let lista = this.activeTab === 'recebidas' ? this.listaRecebidas : this.listaEnviadas;

    if (this.filtroTipo) {
      lista = lista.filter(s => s.tipo === this.filtroTipo);
    }

    if (this.filtroStatus) {
      lista = lista.filter(s => s.status === this.filtroStatus);
    }


    if (this.filtroSolicitante && this.activeTab === 'recebidas') {
      lista = lista.filter(s => 
         s.solicitante?.id.toString() === this.filtroSolicitante
      );
    }

    return lista.sort((a, b) => {
      const dataA = new Date(a.criadoEm).getTime();
      const dataB = new Date(b.criadoEm).getTime();
      
      if (this.ordemData === 'asc') {
        return dataA - dataB;
      } else {
        return dataB - dataA;
      }
    });
  }

  alternarOrdem() {
    this.ordemData = this.ordemData === 'asc' ? 'desc' : 'asc';
  }

  setActiveTab(tab: 'recebidas' | 'enviadas') {
    this.activeTab = tab;
    this.filtroTipo = '';
    this.filtroStatus = '';
    this.filtroSolicitante = '';
  }

  get listaAtual() {
    return this.listaFiltrada;
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
          this.carregarTodos(); 
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
        this.carregarTodos();
      },
      error: () => this.toastr.error('Erro ao rejeitar.')
    });
  }

  getNomeDia(num: number) {
    return this.diasMapa[num] || 'Dia ' + num;
  }
}