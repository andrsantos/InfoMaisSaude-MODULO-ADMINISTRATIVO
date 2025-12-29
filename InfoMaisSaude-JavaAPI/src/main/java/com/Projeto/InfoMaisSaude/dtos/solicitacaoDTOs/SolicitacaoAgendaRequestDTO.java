package com.Projeto.InfoMaisSaude.dtos.solicitacaoDTOs;

import java.util.List;

import com.Projeto.InfoMaisSaude.dtos.medicoDTOs.AgendaItemDTO;

import jakarta.validation.constraints.NotEmpty;

public record SolicitacaoAgendaRequestDTO(
    @NotEmpty(message = "A nova agenda não pode ser vazia")
    List<AgendaItemDTO> novaAgenda,
    
    String justificativa 
) {}