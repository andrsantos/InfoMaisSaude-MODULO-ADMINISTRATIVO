package com.Projeto.InfoMaisSaude.dtos.medicoDTOs;

import java.time.LocalTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record AgendaItemDTO(
    
    @NotNull(message = "O dia da semana é obrigatório")
    @Min(value = 1, message = "O dia deve ser entre 1 (Segunda) e 7 (Domingo)")
    @Max(value = 7, message = "O dia deve ser entre 1 (Segunda) e 7 (Domingo)")
    Integer diaSemana, 

    @NotNull(message = "O horário de início é obrigatório")
    @JsonFormat(pattern = "HH:mm") 
    LocalTime horarioInicio,

    @NotNull(message = "O horário de fim é obrigatório")
    @JsonFormat(pattern = "HH:mm")
    LocalTime horarioFim
) {}