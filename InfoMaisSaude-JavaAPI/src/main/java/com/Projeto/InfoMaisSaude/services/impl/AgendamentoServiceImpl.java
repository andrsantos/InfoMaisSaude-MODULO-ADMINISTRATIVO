package com.Projeto.InfoMaisSaude.services.impl;

import java.nio.file.AccessDeniedException;
import java.text.Normalizer;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.Projeto.InfoMaisSaude.dtos.agendamentoDTOs.AgendamentoRequestDTO;
import com.Projeto.InfoMaisSaude.dtos.agendamentoDTOs.AgendamentoResponseDTO;
import com.Projeto.InfoMaisSaude.dtos.agendamentoDTOs.SlotDisponivelDTO;
import com.Projeto.InfoMaisSaude.dtos.consultaDTOs.ConsultaAgendadaDTO;
import com.Projeto.InfoMaisSaude.dtos.consultaDTOs.ConsultaListagemDTO;
import com.Projeto.InfoMaisSaude.dtos.consultaDTOs.FinalizarConsultaDTO;
import com.Projeto.InfoMaisSaude.dtos.consultaDTOs.NotificacaoCancelamentoDTO;
import com.Projeto.InfoMaisSaude.dtos.consultaDTOs.NotificacaoPosConsultaDTO;
import com.Projeto.InfoMaisSaude.entities.AgendaMedica;
import com.Projeto.InfoMaisSaude.entities.Clinica;
import com.Projeto.InfoMaisSaude.entities.Consulta;
import com.Projeto.InfoMaisSaude.entities.Medico;
import com.Projeto.InfoMaisSaude.entities.Paciente;
import com.Projeto.InfoMaisSaude.entities.Usuario;
import com.Projeto.InfoMaisSaude.enums.StatusConsulta;
import com.Projeto.InfoMaisSaude.enums.UserRole;
import com.Projeto.InfoMaisSaude.repositories.AgendaMedicaRepository;
import com.Projeto.InfoMaisSaude.repositories.ClinicaRepository;
import com.Projeto.InfoMaisSaude.repositories.ConsultaRepository;
import com.Projeto.InfoMaisSaude.repositories.MedicosRepository;
import com.Projeto.InfoMaisSaude.repositories.PacienteRepository;
import com.Projeto.InfoMaisSaude.repositories.UsuariosRepository;
import com.Projeto.InfoMaisSaude.services.AgendamentoService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AgendamentoServiceImpl implements AgendamentoService {

    private final AgendaMedicaRepository agendaMedicaRepository;
    private final ConsultaRepository consultaRepository;
    private static final int DURACAO_CONSULTA_MINUTOS = 30;
    private final MedicosRepository medicosRepository;
    private final PacienteRepository pacienteRepository;
    private final ClinicaRepository clinicaRepository;
    private final UsuariosRepository usuarioRepository;
    private final String BOT_SERVICE_URL = "https://infomaissaude.com.br//webhook/notificar-encerramento";
    private final RestTemplate restTemplate = new RestTemplate();


    @Override
    public List<LocalTime> listarHorariosDisponiveis(Long medicoId, LocalDate data) {

        int diaSemana = data.getDayOfWeek().getValue();

        List<AgendaMedica> gradeHoraria = agendaMedicaRepository.findByMedicoIdAndDiaSemana(medicoId, diaSemana);

        if (gradeHoraria.isEmpty()) {
            return Collections.emptyList();
        }

        List<Consulta> consultasAgendadas = consultaRepository.findByMedicoIdAndDataConsultaAndStatusNot(
                medicoId, 
                data, 
                StatusConsulta.CANCELADA_PELO_PACIENTE 
        );

        List<LocalTime> horariosLivres = new ArrayList<>();

        for (AgendaMedica bloco : gradeHoraria) {
            LocalTime slotAtual = bloco.getHorarioInicio();
            LocalTime fimDoBloco = bloco.getHorarioFim();

            while (slotAtual.plusMinutes(DURACAO_CONSULTA_MINUTOS).isBefore(fimDoBloco) || 
                   slotAtual.plusMinutes(DURACAO_CONSULTA_MINUTOS).equals(fimDoBloco)) {

                if (data.isEqual(LocalDate.now()) && slotAtual.isBefore(LocalTime.now())) {
                    slotAtual = slotAtual.plusMinutes(DURACAO_CONSULTA_MINUTOS);
                    continue; 
                }

                if (isHorarioLivre(slotAtual, consultasAgendadas)) {
                    horariosLivres.add(slotAtual);
                }

                slotAtual = slotAtual.plusMinutes(DURACAO_CONSULTA_MINUTOS);
            }
        }
        
        Collections.sort(horariosLivres);
        return horariosLivres;
    }

    @Override
    public List<ConsultaListagemDTO> listarConsultas(Long medicoId, LocalDate data) {
        List<Consulta> consultas;

        if (medicoId != null) {
            consultas = consultaRepository.findByMedicoIdAndDataConsultaOrderByHorarioInicio(medicoId, data);
        } else {
            consultas = consultaRepository.findByDataConsultaOrderByHorarioInicio(data);
        }

        return consultas.stream()
                .map(ConsultaListagemDTO::fromEntity)
                .toList();
    }



    private boolean isHorarioLivre(LocalTime slot, List<Consulta> agendadas) {
        for (Consulta agendada : agendadas) {
            if (agendada.getHorarioInicio().equals(slot)) {
                return false;
            }
        }
        return true;
    }

    @Override
    @Transactional 
    public AgendamentoResponseDTO agendarConsulta(AgendamentoRequestDTO dto) {
        
        var medico = medicosRepository.findById(dto.medicoId())
                .orElseThrow(() -> new EntityNotFoundException("Médico não encontrado"));

        List<StatusConsulta> statusQueOcupam = List.of(
            StatusConsulta.AGENDADA,
            StatusConsulta.CONFIRMADA,
            StatusConsulta.REALIZADA
        );

        boolean ocupado = consultaRepository.existsByMedicoIdAndDataConsultaAndHorarioInicioAndStatusIn(
                dto.medicoId(), 
                dto.data(), 
                dto.horario(),
                statusQueOcupam
        );

        if (ocupado) {
            throw new IllegalStateException("Desculpe, este horário acabou de ser ocupado.");
        }

        Paciente paciente = pacienteRepository.findByCpf(dto.cpf())
                .orElseGet(() -> {
                    Paciente novo = new Paciente();
                    novo.setNome(dto.nomePaciente());
                    novo.setCpf(dto.cpf());
                    return novo;
                });

        paciente.setNome(dto.nomePaciente());
        paciente.setIdade(dto.idade()); 
        paciente.setSexo(dto.sexo());   
        paciente.setTelefone(dto.telefonePaciente());
        pacienteRepository.save(paciente);


        Clinica clinica = clinicaRepository.findById(medico.getClinica().getId()).orElseThrow(() -> new EntityNotFoundException("Clínica não encontrada"));

        Consulta consulta = new Consulta();
        consulta.setMedico(medico);
        consulta.setPaciente(paciente);
        consulta.setDataConsulta(dto.data());
        consulta.setHorarioInicio(dto.horario());
        consulta.setHorarioFim(dto.horario().plusMinutes(30));
        consulta.setStatus(StatusConsulta.AGENDADA);
        consulta.setMotivoOuQueixa(dto.resumoClinico());
        consulta.setClinica(clinica);
        consultaRepository.save(consulta);

        return new AgendamentoResponseDTO(
            consulta.getId(),
            medico.getNome(),
            medico.getEspecializacao(),
            consulta.getDataConsulta(),
            consulta.getHorarioInicio(),
            consulta.getStatus().name()
        );
    }

@Override
    public List<SlotDisponivelDTO> listarProximosHorariosLivres(String especialidade, Long clinicaId) {

        String termoBusca = normalizarTexto(especialidade);

        List<Medico> medicos = medicosRepository.findByClinicaIdAndEspecializacaoContainingIgnoreCase(clinicaId, termoBusca);
        
        List<SlotDisponivelDTO> slotsLivres = new ArrayList<>();
        LocalDate hoje = LocalDate.now();
        
        List<StatusConsulta> statusQueOcupamHorario = List.of(
            StatusConsulta.AGENDADA,
            StatusConsulta.CONFIRMADA,
            StatusConsulta.REALIZADA
        );
        
        for (int i = 0; i < 14; i++) {
            LocalDate dataAnalise = hoje.plusDays(i);
            var diaSemanaJava = dataAnalise.getDayOfWeek(); 
            
            for (Medico medico : medicos) {
                var agendasDoDia = medico.getAgenda().stream()
                    .filter(a -> verificarDiaSemana(a.getDiaSemana(), diaSemanaJava)) 
                    .toList();

                for (AgendaMedica agenda : agendasDoDia) {
                    LocalTime cursor = agenda.getHorarioInicio();
                    
                    while (cursor.isBefore(agenda.getHorarioFim())) {
           
                        boolean ocupado = consultaRepository.existsByMedicoIdAndDataConsultaAndHorarioInicioAndStatusIn(
                            medico.getId(),
                            dataAnalise,
                            cursor,
                            statusQueOcupamHorario
                        );

                        if (!ocupado) {
 
                            slotsLivres.add(new SlotDisponivelDTO(
                                medico.getId(),
                                medico.getNome(),
                                medico.getEspecializacao(), 
                                dataAnalise,
                                cursor,
                                traduzirDiaSemana(diaSemanaJava),
                                clinicaId 
                            ));
                        }
                        
                        cursor = cursor.plusMinutes(30);
                        
                        if (slotsLivres.size() >= 30) return slotsLivres;
                    }
                }
            }
        }
        
        slotsLivres.sort(Comparator.comparing(SlotDisponivelDTO::data)
                .thenComparing(SlotDisponivelDTO::horario));
                
        return slotsLivres;
    }

    @Override
    public List<ConsultaListagemDTO> listarConsultasPorClinica(Long clinicaId, LocalDate data) {
    
    LocalDate dataFiltro = (data != null) ? data : LocalDate.now();
    
    var consultas = consultaRepository.findByClinicaIdAndDataConsultaOrderByHorarioInicioAsc(clinicaId, dataFiltro);
    return consultas.stream()
            .map(ConsultaListagemDTO::fromEntity)
            .toList();

    }

    @Transactional
    public void cancelarConsulta(Long consultaId, String motivo, Long usuarioId) throws AccessDeniedException {

        Consulta consulta = consultaRepository.findById(consultaId)
                .orElseThrow(() -> new EntityNotFoundException("Consulta não encontrada"));
        
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        if (usuario.getRole() == UserRole.MEDICO) {
            if (!consulta.getMedico().getUsuario().getId().equals(usuarioId)) {
                throw new AccessDeniedException("Você só pode cancelar consultas da sua agenda.");
            }
            consulta.setStatus(StatusConsulta.CANCELADA_PELO_MEDICO);
        }
        else if (usuario.getRole() == UserRole.CLINICA || usuario.getRole() == UserRole.ADMIN) {
             consulta.setStatus(StatusConsulta.CANCELADA_PELA_CLINICA);
        }

        consulta.setMotivoCancelamento(motivo);
        consultaRepository.save(consulta);
        enviarNotificacaoCancelamento(consulta, motivo);

    }

    private void enviarNotificacaoCancelamento(Consulta consulta, String motivo) {

        try {

            String dataFormatada = consulta.getDataConsulta()
                .format(java.time.format.DateTimeFormatter.ofPattern("dd/MM"));
            String horaFormatada = consulta.getHorarioInicio().toString();
            String dataHorario = dataFormatada + " às " + horaFormatada;

            String telefone = consulta.getPaciente().getTelefone().replaceAll("\\D", "");

            var payload = new NotificacaoCancelamentoDTO(
                telefone,
                consulta.getPaciente().getNome(),
                consulta.getMedico().getNome(),
                dataHorario,
                motivo
            );

            String botUrl = "infomaissaude.com.br/webhook/notificar-cancelamento"; 
            restTemplate.postForEntity(botUrl, payload, Void.class);
            
            System.out.println("Notificação de cancelamento enviada.");

        } catch (Exception e) {
            System.err.println("Falha ao notificar cancelamento no WhatsApp: " + e.getMessage());
        }
    }



    
    @Override
    public void cancelarConsultaViaPaciente(Long consultaId, String motivo, String telefonePaciente) throws AccessDeniedException {

        Consulta consulta = consultaRepository.findById(consultaId)
                .orElseThrow(() -> new EntityNotFoundException("Consulta não encontrada"));

        String telefoneCadastrado = consulta.getPaciente().getTelefone(); 
        
        String foneBanco = telefoneCadastrado.replaceAll("\\D", "");
        String foneRequest = telefonePaciente.replaceAll("\\D", "");

        if (!foneBanco.equals(foneRequest)) {
            throw new AccessDeniedException("Este número de telefone não tem permissão para cancelar esta consulta.");
        }

        if (consulta.getStatus() == StatusConsulta.REALIZADA || 
            consulta.getStatus() == StatusConsulta.CANCELADA_PELO_PACIENTE || 
            consulta.getStatus() == StatusConsulta.CANCELADA_PELO_MEDICO) {
            throw new IllegalStateException("Consulta já finalizada ou cancelada.");
        }

        consulta.setStatus(StatusConsulta.CANCELADA_PELO_PACIENTE);
        consulta.setMotivoCancelamento(motivo);
        consultaRepository.save(consulta);

    }



    @Override
    public void finalizarConsulta(Long consultaId, FinalizarConsultaDTO dto, Long medicoId) throws AccessDeniedException {

    Consulta consulta = consultaRepository.findById(consultaId)
                .orElseThrow(() -> new EntityNotFoundException("Consulta não encontrada"));

        if (!consulta.getMedico().getUsuario().getId().equals(medicoId)) {
            throw new AccessDeniedException("Você não pode finalizar a consulta de outro médico.");
        }

        if (consulta.getStatus() == StatusConsulta.REALIZADA || 
            consulta.getStatus() == StatusConsulta.CANCELADA_PELO_MEDICO ||
            consulta.getStatus() == StatusConsulta.CANCELADA_PELO_PACIENTE) {
            throw new IllegalStateException("Esta consulta não pode ser finalizada.");
        }

        consulta.setDiagnostico(dto.diagnostico());
        consulta.setPrescricao(dto.prescricao());
        consulta.setStatus(StatusConsulta.REALIZADA); 
        
        consultaRepository.save(consulta);

        if (dto.prescricao() != null && !dto.prescricao().isBlank()) {
            try {
                String telefone = consulta.getPaciente().getTelefone().replaceAll("\\D", ""); 
                
                var payload = new NotificacaoPosConsultaDTO(
                    telefone,
                    consulta.getPaciente().getNome(),
                    consulta.getMedico().getNome(),
                    dto.prescricao()
                );
                
                restTemplate.postForEntity(BOT_SERVICE_URL, payload, Void.class);
                
                System.out.println("Solicitação de notificação enviada ao Bot.");

            } catch (Exception e) {
                System.err.println("Falha ao comunicar com o serviço de Bot: " + e.getMessage());
            }
        }
    }



    private String normalizarTexto(String texto) {
        if (texto == null) return "";
        
        String textoSemUnderline = texto.replace("_", " ");
        
        String normalizado = Normalizer.normalize(textoSemUnderline, Normalizer.Form.NFD);
        
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        String textoSemAcento = pattern.matcher(normalizado).replaceAll("");
        
        return textoSemAcento.trim(); 
    }

    private String traduzirDiaSemana(java.time.DayOfWeek dia) {
        switch (dia) {
            case MONDAY: return "Segunda-feira";
            case TUESDAY: return "Terça-feira";
            case WEDNESDAY: return "Quarta-feira";
            case THURSDAY: return "Quinta-feira";
            case FRIDAY: return "Sexta-feira";
            case SATURDAY: return "Sábado";
            case SUNDAY: return "Domingo";
            default: return "";
        }
    }

    private boolean verificarDiaSemana(int diaBanco, java.time.DayOfWeek diaJava) {
        return diaBanco == diaJava.getValue(); 
    }

@Override
    public List<ConsultaAgendadaDTO> buscarConsultasAtivasPorTelefoneEClinica(String telefone, Long clinicaId) {
        
        String telefoneLimpo = telefone.replaceAll("\\D", "");
        
        List<StatusConsulta> statusAtivos = List.of(
            StatusConsulta.AGENDADA,
            StatusConsulta.CONFIRMADA
        );
        
        List<Consulta> consultas = consultaRepository.findByPacienteTelefoneAndClinicaIdAndStatusIn(
            telefoneLimpo, 
            clinicaId,
            statusAtivos
        );
        
        return consultas.stream()
                .map(c -> new ConsultaAgendadaDTO(
                    c.getId(),
                    c.getMedico().getNome(),
                    c.getMedico().getEspecializacao(), 
                    c.getDataConsulta(),
                    c.getHorarioInicio()
                ))
                .toList();
    }










}