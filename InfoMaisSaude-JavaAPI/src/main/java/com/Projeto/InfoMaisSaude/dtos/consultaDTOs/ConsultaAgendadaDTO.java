package com.Projeto.InfoMaisSaude.dtos.consultaDTOs;

import java.time.LocalDate;
import java.time.LocalTime;

public record ConsultaAgendadaDTO(
    Long id,
    String nomeMedico,
    String especialidade,
    LocalDate data,
    LocalTime horario
) {}
