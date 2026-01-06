package com.Projeto.InfoMaisSaude.services.impl;

import com.Projeto.InfoMaisSaude.dtos.medicoDTOs.AgendaItemDTO;
import com.Projeto.InfoMaisSaude.dtos.solicitacaoDTOs.SolicitacaoClinicaRequestDTO;
import com.Projeto.InfoMaisSaude.dtos.solicitacaoDTOs.SolicitacaoPerfilRequestDTO;
import com.Projeto.InfoMaisSaude.entities.AgendaMedica;
import com.Projeto.InfoMaisSaude.entities.Clinica;
import com.Projeto.InfoMaisSaude.entities.Medico;
import com.Projeto.InfoMaisSaude.entities.Solicitacao;
import com.Projeto.InfoMaisSaude.entities.Usuario;
import com.Projeto.InfoMaisSaude.enums.Especializacao;
import com.Projeto.InfoMaisSaude.enums.StatusSolicitacao;
import com.Projeto.InfoMaisSaude.enums.TipoSolicitacao;
import com.Projeto.InfoMaisSaude.repositories.ClinicaRepository;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor 
public class SolicitacaoServiceImpl implements SolicitacaoService {

    private final SolicitacaoRepository solicitacaoRepository;
    private final UsuariosRepository usuarioRepository;
    private final MedicosRepository medicoRepository;
    private final ObjectMapper objectMapper; 
    private final ClinicaRepository clinicaRepository;
    

    @Override
    @Transactional
    public Solicitacao solicitarAlteracaoClinica(Long usuarioId, SolicitacaoClinicaRequestDTO dto) {
        Usuario solicitante = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        try {
            String jsonClinica = objectMapper.writeValueAsString(dto);

            Solicitacao solicitacao = Solicitacao.builder()
                    .solicitante(solicitante)
                    .tipo(TipoSolicitacao.ALTERACAO_DADOS_CLINICA) 
                    .status(StatusSolicitacao.PENDENTE)
                    .dadosNovos(jsonClinica)
                    .justificativaMedico("Atualização de dados da clínica.")
                    .criadoEm(LocalDateTime.now())
                    .build();

            return solicitacaoRepository.save(solicitacao);

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Erro ao processar dados da clínica", e);
        }
    }

    @Override
    @Transactional
    public Solicitacao solicitarAlteracaoAgenda(Long usuarioId, List<AgendaItemDTO> novaAgenda, String justificativa) {
        
    Usuario solicitante = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

    Medico medico = medicoRepository.findByUsuarioId(usuarioId);

    if(medico == null){
        throw new EntityNotFoundException("Médico não encontrado.");
    }

    Clinica clinica = clinicaRepository.findById(medico.getClinica().getId())
    .orElseThrow(() -> new EntityNotFoundException("Clinica não encontrada."));

    Usuario avaliador = usuarioRepository.findById(clinica.getUsuario().getId())
    .orElseThrow(() -> new EntityNotFoundException("Usuário clínica não encontrado"));


        try {

            String jsonAgenda = objectMapper.writeValueAsString(novaAgenda);
            Solicitacao solicitacao = Solicitacao.builder()
                    .solicitante(solicitante)
                    .avaliador(avaliador)
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

    Medico medico = medicoRepository.findByUsuarioId(usuarioId);

    if(medico == null){
        throw new EntityNotFoundException("Médico não encontrado.");
    }

    Clinica clinica = clinicaRepository.findById(medico.getClinica().getId())
    .orElseThrow(() -> new EntityNotFoundException("Clinica não encontrada."));

    Usuario avaliador = usuarioRepository.findById(clinica.getUsuario().getId())
    .orElseThrow(() -> new EntityNotFoundException("Usuário clínica não encontrado"));



        try {
            String jsonPerfil = objectMapper.writeValueAsString(dto);

            Solicitacao solicitacao = Solicitacao.builder()
                    .solicitante(solicitante)
                    .avaliador(avaliador)
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
        else if (solicitacao.getTipo() == TipoSolicitacao.ALTERACAO_DADOS_CLINICA) {
            aplicarMudancaDeClinica(solicitacao); 
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

    private void aplicarMudancaDeClinica(Solicitacao solicitacao) {

        Clinica clinica = clinicaRepository.findByUsuarioId(solicitacao.getSolicitante().getId())
        .orElseThrow(() ->  new EntityNotFoundException("Clinica não encontrada para esta solicitação."));

        try {
            SolicitacaoClinicaRequestDTO dto = objectMapper.readValue(
                    solicitacao.getDadosNovos(),
                    SolicitacaoClinicaRequestDTO.class
            );

            clinica.setNome(dto.nome());
            clinica.setCnpj(dto.cnpj());
            clinica.setEmail(dto.email());
            clinica.setTelefone(dto.telefone());
            clinica.setEndereco(dto.endereco());
            clinica.setSite(dto.site());
            clinica.setHorarioFuncionamentoInicio(dto.horarioFuncionamentoInicio());
            clinica.setHorarioFuncionamentoFinal(dto.horarioFuncionamentoFinal());
            clinica.setLatitude(dto.latitude());
            clinica.setLongitude(dto.longitude());

            if (dto.especializacoes() != null) {
                clinica.setEspecializacoes(
                    dto.especializacoes().stream()
                       .map(Especializacao::valueOf)
                       .collect(Collectors.toSet())
                );
            }

            clinicaRepository.save(clinica);

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Erro ao ler JSON da clínica", e);
        }
    }

    @Override
    public List<Solicitacao> listarTodos(Long id) {
     
     List<Solicitacao> todos = new ArrayList<>();
     List<Solicitacao> enviadas = solicitacaoRepository.findBySolicitanteId(id);
     List<Solicitacao> recebidas = solicitacaoRepository.findByAvaliadorId(id);

     if(enviadas == null){
        throw new EntityNotFoundException("Lista de enviadas retornou nulo");
     }
     if(recebidas == null){
        throw new EntityNotFoundException("Lista de recebidas retornou nulo");
     }
     enviadas.forEach(enviada -> {
        todos.add(enviada);
     });
     recebidas.forEach( recebida -> {
        todos.add(recebida);
     });
     return todos;

    }

}