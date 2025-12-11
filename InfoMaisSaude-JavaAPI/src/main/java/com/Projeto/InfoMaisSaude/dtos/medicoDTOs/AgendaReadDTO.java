package com.Projeto.InfoMaisSaude.dtos.medicoDTOs;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalTime;

public record AgendaReadDTO(
    Long id,               
    Integer diaSemana,     
    
    @JsonFormat(pattern = "HH:mm")
    LocalTime horarioInicio,

    @JsonFormat(pattern = "HH:mm")
    LocalTime horarioFim
) {}

