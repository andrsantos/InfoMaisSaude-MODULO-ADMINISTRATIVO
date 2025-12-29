package com.Projeto.InfoMaisSaude.dtos.agendamentoDTOs;

import java.time.LocalDate;
import java.time.LocalTime;

public record AgendamentoResponseDTO(
    Long idConsulta,
    String nomeMedico,
    String especialidade,
    LocalDate data,
    LocalTime horario,
    String status
) {}