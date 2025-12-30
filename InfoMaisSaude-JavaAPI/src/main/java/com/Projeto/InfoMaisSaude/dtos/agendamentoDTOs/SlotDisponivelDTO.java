package com.Projeto.InfoMaisSaude.dtos.agendamentoDTOs;

import java.time.LocalDate;
import java.time.LocalTime;

public record SlotDisponivelDTO(
    Long medicoId,
    String nomeMedico,
    String especialidade,
    LocalDate data,       
    LocalTime horario,    
    String diaDaSemana    
) {}