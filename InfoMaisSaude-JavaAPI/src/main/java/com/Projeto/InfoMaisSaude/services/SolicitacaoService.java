package com.Projeto.InfoMaisSaude.services;

import java.util.List;
import com.Projeto.InfoMaisSaude.dtos.medicoDTOs.AgendaItemDTO;
import com.Projeto.InfoMaisSaude.dtos.solicitacaoDTOs.SolicitacaoPerfilRequestDTO;
import com.Projeto.InfoMaisSaude.entities.Solicitacao;

public interface SolicitacaoService {
    
    Solicitacao solicitarAlteracaoAgenda(Long usuarioId, List<AgendaItemDTO> novaAgenda, String justificativa);
    Solicitacao solicitarAlteracaoPerfil(Long usuarioId, SolicitacaoPerfilRequestDTO dto);
    void aprovarSolicitacao(Long solicitacaoId, Long adminId);
    void rejeitarSolicitacao(Long solicitacaoId, Long adminId, String motivo);
    List<Solicitacao> listarPendentes();
    List<Solicitacao> listarHistoricoDoUsuario(Long usuarioId);
    
}