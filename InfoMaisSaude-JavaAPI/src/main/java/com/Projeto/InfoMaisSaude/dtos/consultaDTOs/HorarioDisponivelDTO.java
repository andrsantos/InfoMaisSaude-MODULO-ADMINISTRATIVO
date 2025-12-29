package com.Projeto.InfoMaisSaude.dtos.consultaDTOs;

import java.time.LocalTime;

public record HorarioDisponivelDTO(
    LocalTime horario,
    boolean disponivel
) {}