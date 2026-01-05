package com.Projeto.InfoMaisSaude.controllers;

import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.Projeto.InfoMaisSaude.dtos.agendamentoDTOs.AgendamentoRequestDTO;
import com.Projeto.InfoMaisSaude.dtos.agendamentoDTOs.AgendamentoResponseDTO;
import com.Projeto.InfoMaisSaude.dtos.agendamentoDTOs.SlotDisponivelDTO;
import com.Projeto.InfoMaisSaude.dtos.consultaDTOs.CancelamentoRequestDTO;
import com.Projeto.InfoMaisSaude.dtos.consultaDTOs.ConsultaAgendadaDTO;
import com.Projeto.InfoMaisSaude.dtos.consultaDTOs.ConsultaListagemDTO;
import com.Projeto.InfoMaisSaude.dtos.consultaDTOs.FinalizarConsultaDTO;
import com.Projeto.InfoMaisSaude.entities.Usuario;
import com.Projeto.InfoMaisSaude.repositories.MedicosRepository;
import com.Projeto.InfoMaisSaude.services.AgendamentoService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/agendamentos")
@RequiredArgsConstructor
public class AgendamentoController {

    private final AgendamentoService agendamentoService;

    private final MedicosRepository medicosRepository;

    @GetMapping
    @PreAuthorize("hasAnyRole('MEDICO', 'CLINICA', 'ADMIN')")
    public ResponseEntity<List<ConsultaListagemDTO>> listarConsultas(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data,
            @RequestParam(required = false) Long medicoIdFiltro, 
            @AuthenticationPrincipal Usuario usuarioLogado
    ) {
        LocalDate dataBusca = (data != null) ? data : LocalDate.now();
        
        Long idParaBuscar = null;

        if (usuarioLogado.getRole().name().equals("MEDICO")) {
            var medico = Optional.ofNullable(medicosRepository.findByUsuarioId(usuarioLogado.getId()))
                    .orElseThrow(() -> new EntityNotFoundException("Médico não encontrado para este usuário"));
            idParaBuscar = medico.getId();
        } else {
            idParaBuscar = medicoIdFiltro;
        }

        var consultas = agendamentoService.listarConsultas(idParaBuscar, dataBusca);
        return ResponseEntity.ok(consultas);
    }

    @PostMapping("/agendar")
    public ResponseEntity<AgendamentoResponseDTO> agendarConsulta(
            @RequestBody @Valid AgendamentoRequestDTO dto
    ) {
        var agendamento = agendamentoService.agendarConsulta(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(agendamento);
    }


    @GetMapping("/disponibilidade")
    public ResponseEntity<List<LocalTime>> checarDisponibilidade(
            @RequestParam Long medicoId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data
    ) {
        var horarios = agendamentoService.listarHorariosDisponiveis(medicoId, data);
        return ResponseEntity.ok(horarios);
    }

@GetMapping("/disponibilidade-combo")
    public ResponseEntity<List<SlotDisponivelDTO>> getDisponibilidadeCombo(
            @RequestParam String especialidade,
            @RequestParam Long clinicaId 
    ) {
        var slots = agendamentoService.listarProximosHorariosLivres(especialidade, clinicaId);
        return ResponseEntity.ok(slots);
    }

    @GetMapping("/consultas-clinica/{clinicaId}")
    public ResponseEntity<List<ConsultaListagemDTO>> getConsultasPorClinica(@PathVariable Long clinicaId,
    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data
    ){  
        System.out.println("clinica id" + clinicaId);
        var consultasPorClinica = agendamentoService.listarConsultasPorClinica(clinicaId, data);
        return ResponseEntity.ok(consultasPorClinica);
    }

    @PostMapping("/{id}/cancelar")
    public ResponseEntity<Void> cancelarConsulta(
            @PathVariable Long id,
            @RequestBody CancelamentoRequestDTO dto,
            @AuthenticationPrincipal Usuario usuarioLogado
    ) {
        try {
            agendamentoService.cancelarConsulta(id, dto.motivo(), usuarioLogado.getId());
        } catch (AccessDeniedException e) {
            e.printStackTrace();
        }
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/cancelar-paciente")
    public ResponseEntity<Void> cancelarViaPaciente(
            @PathVariable Long id,
            @RequestBody CancelamentoRequestDTO dto, 
            @RequestParam String telefone 
    ) {
        try {
            agendamentoService.cancelarConsultaViaPaciente(id, dto.motivo(), telefone);
        } catch (AccessDeniedException e) {
            e.printStackTrace();
        }
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/paciente/{telefone}/clinica/{clinicaId}/ativas")
    public ResponseEntity<List<ConsultaAgendadaDTO>> buscarConsultasAtivasPaciente(
            @PathVariable String telefone,
            @PathVariable Long clinicaId
    ) {
        var consultas = agendamentoService.buscarConsultasAtivasPorTelefoneEClinica(telefone, clinicaId);
        return ResponseEntity.ok(consultas);
    }

    @PostMapping("/{id}/finalizar")
    @PreAuthorize("hasRole('MEDICO')")
    public ResponseEntity<Void> finalizarConsulta(
            @PathVariable Long id,
            @RequestBody FinalizarConsultaDTO dto,
            @AuthenticationPrincipal Usuario usuarioLogado
    ) {
        try {
            agendamentoService.finalizarConsulta(id, dto, usuarioLogado.getId());
        } catch (AccessDeniedException e) {
            e.printStackTrace();
        }
        return ResponseEntity.noContent().build();
    }
    
}