package com.Projeto.InfoMaisSaude.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.Projeto.InfoMaisSaude.entities.Solicitacao;
import com.Projeto.InfoMaisSaude.enums.StatusSolicitacao;
import java.util.List;

@Repository
public interface SolicitacaoRepository extends JpaRepository<Solicitacao, Long> {

    List<Solicitacao> findByStatus(StatusSolicitacao status);
    List<Solicitacao> findBySolicitanteIdOrderByCriadoEmDesc(Long solicitanteId);
    List<Solicitacao> findBySolicitanteIdAndStatus(Long solicitanteId, StatusSolicitacao status);
}