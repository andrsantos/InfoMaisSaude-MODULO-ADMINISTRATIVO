package com.Projeto.InfoMaisSaude.controllers;

import com.Projeto.InfoMaisSaude.dtos.solicitacaoDTOs.SolicitacaoAgendaRequestDTO;
import com.Projeto.InfoMaisSaude.dtos.solicitacaoDTOs.SolicitacaoPerfilRequestDTO;
import com.Projeto.InfoMaisSaude.dtos.solicitacaoDTOs.SolicitacaoRejeicaoRequestDTO;
import com.Projeto.InfoMaisSaude.dtos.solicitacaoDTOs.SolicitacaoResponseDTO;
import com.Projeto.InfoMaisSaude.entities.Usuario;
import com.Projeto.InfoMaisSaude.services.SolicitacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/solicitacoes")
@RequiredArgsConstructor
public class SolicitacaoController {

    private final SolicitacaoService solicitacaoService;

    @PostMapping("/agenda")
    @PreAuthorize("hasRole('MEDICO')") 
    public ResponseEntity<SolicitacaoResponseDTO> solicitarAlteracaoAgenda(
            @RequestBody SolicitacaoAgendaRequestDTO dto,
            @AuthenticationPrincipal Usuario usuarioLogado 
    ) {

        System.out.println("--- DEBUG SECURITY ---");
        System.out.println("Usuário: " + usuarioLogado.getUsername()); 
        System.out.println("Role no Objeto: " + usuarioLogado.getRole());
        System.out.println("Authorities Geradas: " + usuarioLogado.getAuthorities());
        System.out.println("----------------------");
        
        var solicitacao = solicitacaoService.solicitarAlteracaoAgenda(
                usuarioLogado.getId(),
                dto.novaAgenda(),
                dto.justificativa()
        );
       return ResponseEntity.status(HttpStatus.CREATED)
                .body(SolicitacaoResponseDTO.fromEntity(solicitacao));
    }

    @PostMapping("/perfil")
    @PreAuthorize("hasRole('MEDICO')")
    public ResponseEntity<SolicitacaoResponseDTO> solicitarAlteracaoPerfil(
            @RequestBody SolicitacaoPerfilRequestDTO dto,
            @AuthenticationPrincipal Usuario usuarioLogado
    ) {
        var solicitacao = solicitacaoService.solicitarAlteracaoPerfil(
                usuarioLogado.getId(),
                dto
        );
        var responseDTO = SolicitacaoResponseDTO.fromEntity(solicitacao);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

   @GetMapping("/meus-pedidos")
    @PreAuthorize("hasRole('MEDICO')") 
    public ResponseEntity<List<SolicitacaoResponseDTO>> verMeusPedidos( 
            @AuthenticationPrincipal Usuario usuarioLogado
    ) {
        var historico = solicitacaoService.listarHistoricoDoUsuario(usuarioLogado.getId());
        
        var historicoDTO = historico.stream()
                .map(SolicitacaoResponseDTO::fromEntity)
                .toList();

        return ResponseEntity.ok(historicoDTO);
    }

    @GetMapping("/pendentes")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLINICA')") 
    public ResponseEntity<List<SolicitacaoResponseDTO>> listarPendentes() { 
        var pendentes = solicitacaoService.listarPendentes();
        var pendentesDTO = pendentes.stream()
                .map(SolicitacaoResponseDTO::fromEntity)
                .toList();

        return ResponseEntity.ok(pendentesDTO);
    }

    @PostMapping("/{id}/aprovar")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLINICA')")
    public ResponseEntity<Void> aprovarSolicitacao(
            @PathVariable Long id,
            @AuthenticationPrincipal Usuario adminLogado
    ) {
        solicitacaoService.aprovarSolicitacao(id, adminLogado.getId());
        return ResponseEntity.noContent().build(); 
    }

    @PostMapping("/{id}/rejeitar")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLINICA')")
    public ResponseEntity<Void> rejeitarSolicitacao(
            @PathVariable Long id,
            @RequestBody SolicitacaoRejeicaoRequestDTO dto,
            @AuthenticationPrincipal Usuario adminLogado
    ) {
        solicitacaoService.rejeitarSolicitacao(id, adminLogado.getId(), dto.motivo());
        return ResponseEntity.noContent().build();
    }
}