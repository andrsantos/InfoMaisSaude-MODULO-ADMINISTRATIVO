import { Usuario } from '../usuarioModels/usuario.model';

export interface Solicitacao {
  id: number;
  tipo: 'ALTERACAO_AGENDA' | 'ALTERACAO_DADOS_CADASTRAIS' | 'ALTERACAO_DADOS_CLINICA';
  status: 'PENDENTE' | 'APROVADO' | 'REJEITADO';
  dadosNovos: string;
  justificativaMedico?: string;
  motivoRejeicao?: string;
  solicitante?: Usuario; 
  avaliador?: Usuario | null; 
  criadoEm: string;
  avaliadoEm?: string;
}