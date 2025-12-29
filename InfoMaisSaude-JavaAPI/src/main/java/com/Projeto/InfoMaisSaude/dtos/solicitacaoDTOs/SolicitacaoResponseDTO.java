package com.Projeto.InfoMaisSaude.dtos.solicitacaoDTOs;


import java.time.LocalDateTime;

import com.Projeto.InfoMaisSaude.entities.Solicitacao;
import com.Projeto.InfoMaisSaude.enums.StatusSolicitacao;
import com.Projeto.InfoMaisSaude.enums.TipoSolicitacao;

public record SolicitacaoResponseDTO(
    Long id,
    TipoSolicitacao tipo,
    StatusSolicitacao status,
    String dadosNovos,
    String justificativaMedico,
    String motivoRejeicao,
    UsuarioResumoDTO solicitante, 
    UsuarioResumoDTO avaliador,   
    LocalDateTime criadoEm,
    LocalDateTime avaliadoEm
) {
    public static SolicitacaoResponseDTO fromEntity(Solicitacao s) {
        return new SolicitacaoResponseDTO(
            s.getId(),
            s.getTipo(),
            s.getStatus(),
            s.getDadosNovos(),
            s.getJustificativaMedico(),
            s.getMotivoRejeicao(),
            UsuarioResumoDTO.fromEntity(s.getSolicitante()), 
            UsuarioResumoDTO.fromEntity(s.getAvaliador()),   
            s.getCriadoEm(),
            s.getAvaliadoEm()
        );
    }
}