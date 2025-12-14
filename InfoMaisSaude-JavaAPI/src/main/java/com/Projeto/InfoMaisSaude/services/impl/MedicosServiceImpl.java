package com.Projeto.InfoMaisSaude.services.impl;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.Projeto.InfoMaisSaude.dtos.medicoDTOs.*;
import com.Projeto.InfoMaisSaude.entities.AgendaMedica;
import com.Projeto.InfoMaisSaude.entities.Medico;
import com.Projeto.InfoMaisSaude.entities.Usuario;
import com.Projeto.InfoMaisSaude.enums.UserRole;
import com.Projeto.InfoMaisSaude.repositories.MedicosRepository;
import com.Projeto.InfoMaisSaude.repositories.UsuariosRepository;
import com.Projeto.InfoMaisSaude.services.MedicosService;
import jakarta.persistence.EntityNotFoundException; 
import org.springframework.security.crypto.password.PasswordEncoder; 


@Service
public class MedicosServiceImpl implements MedicosService {

    @Autowired
    MedicosRepository medicosRepository;

    @Autowired
    UsuariosRepository usuariosRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public MedicoResponseCreateDTO criarMedico(MedicoCreateDTO dto) {
        
        if (medicosRepository.existsByTelefone(dto.getTelefone())) {
            throw new IllegalArgumentException("Erro: Já existe um médico com este telefone.");
        }

        if (usuariosRepository.findByLogin(dto.getLogin()) != null) {
            throw new IllegalArgumentException("Erro: Este login já está em uso por outro usuário.");
        }

       if (dto.getAgenda() != null && !dto.getAgenda().isEmpty()) {
            validarConflitoDeHorarios(dto.getAgenda());
        }

        Usuario novoUsuario = new Usuario();
        novoUsuario.setLogin(dto.getLogin()); 
        novoUsuario.setSenha(passwordEncoder.encode(dto.getSenha()));
        novoUsuario.setRole(UserRole.MEDICO); 
        novoUsuario = usuariosRepository.save(novoUsuario);

        Medico medico = new Medico();
        medico.setNome(dto.getNome());
        medico.setEspecializacao(dto.getEspecializacao());
        medico.setTelefone(dto.getTelefone());
        medico.setUsuario(novoUsuario);
        medico.setAgenda(new ArrayList<>()); 

        if (dto.getAgenda() != null) {
            dto.getAgenda().forEach(itemAgenda -> {
                AgendaMedica novaAgenda = new AgendaMedica();
                novaAgenda.setDiaSemana(itemAgenda.diaSemana());
                novaAgenda.setHorarioInicio(itemAgenda.horarioInicio());
                novaAgenda.setHorarioFim(itemAgenda.horarioFim());
                medico.adicionarHorario(novaAgenda);
            });
        }
        medicosRepository.save(medico);
        return new MedicoResponseCreateDTO(
            medico.getNome(),
            "Médico criado com sucesso"
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<MedicoResponseReadDTO> listarMedicos() {
        List<Medico> listaMedicos = medicosRepository.findAll();

        return listaMedicos.stream()
            .map(this::converterParaReadDTO)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public MedicoResponseReadDTO pegarMedico(Long id) {
        Medico medico = medicosRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Médico não encontrado com ID: " + id));

        return converterParaReadDTO(medico);
    }

    @Override
    @Transactional
    public MedicoResponseUpdateDTO atualizarMedico(Long id, MedicoUpdateDTO dto) {
        Medico medico = medicosRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Médico não encontrado para atualização"));

        medico.setNome(dto.getNome());
        medico.setEspecializacao(dto.getEspecializacao());
        medico.setTelefone(dto.getTelefone());

        if (dto.getAgenda() != null) {

            validarConflitoDeHorarios(dto.getAgenda());

            medico.getAgenda().clear(); 

            dto.getAgenda().forEach(itemDto -> {
                AgendaMedica agendaNova = new AgendaMedica();
                agendaNova.setDiaSemana(itemDto.diaSemana()); 
                agendaNova.setHorarioInicio(itemDto.horarioInicio());
                agendaNova.setHorarioFim(itemDto.horarioFim());
                medico.adicionarHorario(agendaNova);
            });
        }

        medicosRepository.save(medico);

        return new MedicoResponseUpdateDTO(
            medico.getNome(),
            "Médico atualizado com sucesso."
        );
    }

    @Override
    @Transactional
    public MedicoResponseDeleteDTO deletarMedico(Long id) {
        if (!medicosRepository.existsById(id)) {
            throw new EntityNotFoundException("Médico não encontrado para deleção");
        }
        Medico medicoDeletado = medicosRepository.findById(id).get();
        medicosRepository.deleteById(id);
        return new MedicoResponseDeleteDTO(medicoDeletado.getNome(),"Médico deletado com sucesso.");
    }

    @Override
    @Transactional
    public MedicoResponseReadDTO pegarMedicoPorUsuario(Long idUsuario) {
        Medico medico = medicosRepository.findByUsuarioId(idUsuario);
        if (medico == null) {
            throw new EntityNotFoundException("Médico não encontrado para o ID de usuário: " + idUsuario);
        }
        return converterParaReadDTO(medico);
    }


    private MedicoResponseReadDTO converterParaReadDTO(Medico medico) {
        List<AgendaReadDTO> agendaDtos = medico.getAgenda().stream()
            .map(agenda -> new AgendaReadDTO(
                agenda.getId(),
                agenda.getDiaSemana(), 
                agenda.getHorarioInicio(),
                agenda.getHorarioFim()
            ))
            .collect(Collectors.toList());

        return new MedicoResponseReadDTO(
            medico.getId(),
            medico.getNome(),
            medico.getEspecializacao(),
            medico.getTelefone(),
            agendaDtos,
            medico.getUsuario().getLogin(),
            medico.getUsuario().getSenha()
        );
    }

  
    private void validarConflitoDeHorarios(List<? extends AgendaItemDTO> agendaItems) {
        if (agendaItems == null || agendaItems.isEmpty()) return;

        var itensPorDia = agendaItems.stream()
                .collect(Collectors.groupingBy(item -> item.diaSemana()));

        for (var entry : itensPorDia.entrySet()) {
            var horariosDoDia = entry.getValue();
            if (horariosDoDia.size() < 2) continue;

            for (int i = 0; i < horariosDoDia.size(); i++) {
                for (int j = i + 1; j < horariosDoDia.size(); j++) {
                    var h1 = horariosDoDia.get(i);
                    var h2 = horariosDoDia.get(j);

                    if (h1.horarioInicio().isBefore(h2.horarioFim()) &&
                        h2.horarioInicio().isBefore(h1.horarioFim())) {
                        throw new IllegalArgumentException(
                            "Conflito de horários no dia " + DayOfWeek.of(entry.getKey())
                        );
                    }
                }
            }
        }
    }
}