package com.Projeto.InfoMaisSaude.services.impl;

import com.Projeto.InfoMaisSaude.dtos.medicoDTOs.AgendaItemDTO;
import com.Projeto.InfoMaisSaude.dtos.solicitacaoDTOs.SolicitacaoPerfilRequestDTO;
import com.Projeto.InfoMaisSaude.entities.AgendaMedica;
import com.Projeto.InfoMaisSaude.entities.Medico;
import com.Projeto.InfoMaisSaude.entities.Solicitacao;
import com.Projeto.InfoMaisSaude.entities.Usuario;
import com.Projeto.InfoMaisSaude.enums.StatusSolicitacao;
import com.Projeto.InfoMaisSaude.enums.TipoSolicitacao;
import com.Projeto.InfoMaisSaude.repositories.MedicosRepository;
import com.Projeto.InfoMaisSaude.repositories.SolicitacaoRepository;
import com.Projeto.InfoMaisSaude.repositories.UsuariosRepository;
import com.Projeto.InfoMaisSaude.services.SolicitacaoService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor 
public class SolicitacaoServiceImpl implements SolicitacaoService {

    private final SolicitacaoRepository solicitacaoRepository;
    private final UsuariosRepository usuarioRepository;
    private final MedicosRepository medicoRepository;
    private final ObjectMapper objectMapper; 

    @Override
    @Transactional
    public Solicitacao solicitarAlteracaoAgenda(Long usuarioId, List<AgendaItemDTO> novaAgenda, String justificativa) {
        
        Usuario solicitante = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        try {

            String jsonAgenda = objectMapper.writeValueAsString(novaAgenda);
            Solicitacao solicitacao = Solicitacao.builder()
                    .solicitante(solicitante)
                    .tipo(TipoSolicitacao.ALTERACAO_AGENDA)
                    .status(StatusSolicitacao.PENDENTE)
                    .dadosNovos(jsonAgenda) 
                    .justificativaMedico(justificativa)
                    .criadoEm(LocalDateTime.now())
                    .build();

            return solicitacaoRepository.save(solicitacao);

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Erro ao processar dados da agenda", e);
        }
    }

    @Override
    @Transactional
    public Solicitacao solicitarAlteracaoPerfil(Long usuarioId, SolicitacaoPerfilRequestDTO dto) {
    Usuario solicitante = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        try {
            String jsonPerfil = objectMapper.writeValueAsString(dto);

            Solicitacao solicitacao = Solicitacao.builder()
                    .solicitante(solicitante)
                    .tipo(TipoSolicitacao.ALTERACAO_DADOS_CADASTRAIS) 
                    .status(StatusSolicitacao.PENDENTE)
                    .dadosNovos(jsonPerfil)
                    .justificativaMedico("Alteração de dados cadastrais solicitada pelo médico.")
                    .criadoEm(LocalDateTime.now())
                    .build();

            return solicitacaoRepository.save(solicitacao);

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Erro ao processar dados do perfil", e);
        }
    }

    @Override
    @Transactional
    public void aprovarSolicitacao(Long solicitacaoId, Long adminId) {
        
        Solicitacao solicitacao = solicitacaoRepository.findById(solicitacaoId)
                .orElseThrow(() -> new EntityNotFoundException("Solicitação não encontrada"));

        if (solicitacao.getStatus() != StatusSolicitacao.PENDENTE) {
            throw new IllegalStateException("Esta solicitação já foi processada.");
        }

        Usuario admin = usuarioRepository.findById(adminId)
                .orElseThrow(() -> new EntityNotFoundException("Admin não encontrado"));
                
        if (solicitacao.getTipo() == TipoSolicitacao.ALTERACAO_AGENDA) {
            aplicarMudancaDeAgenda(solicitacao);
        } 
        else if (solicitacao.getTipo() == TipoSolicitacao.ALTERACAO_DADOS_CADASTRAIS) {
            aplicarMudancaDePerfil(solicitacao); 
        }

        solicitacao.setStatus(StatusSolicitacao.APROVADO);
        solicitacao.setAvaliador(admin);
        solicitacao.setAvaliadoEm(LocalDateTime.now());
        
        solicitacaoRepository.save(solicitacao);
    }

    @Override
    @Transactional
    public void rejeitarSolicitacao(Long solicitacaoId, Long adminId, String motivo) {
        Solicitacao solicitacao = solicitacaoRepository.findById(solicitacaoId)
                .orElseThrow(() -> new EntityNotFoundException("Solicitação não encontrada"));

        Usuario admin = usuarioRepository.findById(adminId)
                .orElseThrow(() -> new EntityNotFoundException("Admin não encontrado"));

        solicitacao.setStatus(StatusSolicitacao.REJEITADO);
        solicitacao.setAvaliador(admin);
        solicitacao.setAvaliadoEm(LocalDateTime.now());
        solicitacao.setMotivoRejeicao(motivo);

        solicitacaoRepository.save(solicitacao);
    }

    @Override
    public List<Solicitacao> listarPendentes() {
        return solicitacaoRepository.findByStatus(StatusSolicitacao.PENDENTE);
    }

    @Override
    public List<Solicitacao> listarHistoricoDoUsuario(Long usuarioId) {
        return solicitacaoRepository.findBySolicitanteIdOrderByCriadoEmDesc(usuarioId);
    }

    private void aplicarMudancaDeAgenda(Solicitacao solicitacao) {

        Medico medico = Optional.ofNullable(medicoRepository.findByUsuarioId(solicitacao.getSolicitante().getId()))
                .orElseThrow(() -> new EntityNotFoundException("Perfil de médico não encontrado para este usuário"));

        try {
            List<AgendaItemDTO> agendaNovaDto = objectMapper.readValue(
                    solicitacao.getDadosNovos(), 
                    new TypeReference<List<AgendaItemDTO>>() {}
            );

            medico.getAgenda().clear(); 

            agendaNovaDto.forEach(dto -> {
                AgendaMedica agenda = new AgendaMedica();
                agenda.setDiaSemana(dto.diaSemana()); 
                agenda.setHorarioInicio(dto.horarioInicio());
                agenda.setHorarioFim(dto.horarioFim());
                
                medico.adicionarHorario(agenda); 
            });

            medicoRepository.save(medico);

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Erro ao ler JSON da agenda", e);
        }
    }

    private void aplicarMudancaDePerfil(Solicitacao solicitacao) {
        Medico medico = Optional.ofNullable(medicoRepository.findByUsuarioId(solicitacao.getSolicitante().getId()))
                .orElseThrow(() -> new EntityNotFoundException("Médico não encontrado"));
        
        Usuario usuario = medico.getUsuario(); 

        try {
            SolicitacaoPerfilRequestDTO dto = objectMapper.readValue(
                    solicitacao.getDadosNovos(),
                    SolicitacaoPerfilRequestDTO.class
            );
            
            medico.setNome(dto.nome());
            medico.setEspecializacao(dto.especializacao());
            medico.setTelefone(dto.telefone());
            
            if (!usuario.getLogin().equals(dto.login())) {
                 usuario.setLogin(dto.login());
                 usuarioRepository.save(usuario);
            }

            medicoRepository.save(medico);

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Erro ao ler JSON do perfil", e);
        }
    }
}