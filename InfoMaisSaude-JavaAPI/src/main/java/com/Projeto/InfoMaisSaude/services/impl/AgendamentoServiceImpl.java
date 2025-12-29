package com.Projeto.InfoMaisSaude.services.impl;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import com.Projeto.InfoMaisSaude.dtos.agendamentoDTOs.AgendamentoRequestDTO;
import com.Projeto.InfoMaisSaude.dtos.agendamentoDTOs.AgendamentoResponseDTO;
import com.Projeto.InfoMaisSaude.dtos.consultaDTOs.ConsultaListagemDTO;
import com.Projeto.InfoMaisSaude.entities.AgendaMedica;
import com.Projeto.InfoMaisSaude.entities.Consulta;
import com.Projeto.InfoMaisSaude.entities.Paciente;
import com.Projeto.InfoMaisSaude.enums.StatusConsulta;
import com.Projeto.InfoMaisSaude.repositories.AgendaMedicaRepository;
import com.Projeto.InfoMaisSaude.repositories.ConsultaRepository;
import com.Projeto.InfoMaisSaude.repositories.MedicosRepository;
import com.Projeto.InfoMaisSaude.repositories.PacienteRepository;
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

        boolean ocupado = consultaRepository.existsByMedicoIdAndDataConsultaAndHorarioInicioAndStatusNot(
                dto.medicoId(), 
                dto.data(), 
                dto.horario(),
                StatusConsulta.CANCELADA_PELO_PACIENTE 
        );

        if (ocupado) {
            throw new IllegalStateException("Desculpe, este horário acabou de ser ocupado.");
        }

        Paciente paciente = pacienteRepository.findByTelefone(dto.telefonePaciente())
                .orElseGet(() -> {
                    Paciente novo = new Paciente();
                    novo.setNome(dto.nomePaciente());
                    novo.setTelefone(dto.telefonePaciente());
                    return novo;
                });

        paciente.setNome(dto.nomePaciente());
        paciente.setIdade(dto.idade()); 
        paciente.setSexo(dto.sexo());   
        pacienteRepository.save(paciente);

        Consulta consulta = new Consulta();
        consulta.setMedico(medico);
        consulta.setPaciente(paciente);
        consulta.setDataConsulta(dto.data());
        consulta.setHorarioInicio(dto.horario());
        consulta.setHorarioFim(dto.horario().plusMinutes(30));
        consulta.setStatus(StatusConsulta.AGENDADA);
        consulta.setMotivoOuQueixa(dto.resumoClinico());
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
}