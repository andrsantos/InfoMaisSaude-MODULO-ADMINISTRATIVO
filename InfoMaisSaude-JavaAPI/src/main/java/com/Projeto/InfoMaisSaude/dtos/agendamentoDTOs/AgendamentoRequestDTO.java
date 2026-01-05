package com.Projeto.InfoMaisSaude.dtos.agendamentoDTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

public record AgendamentoRequestDTO(
    @NotNull Long clinicaId,
    @NotNull Long medicoId,
    @NotNull LocalDate data,
    @NotNull LocalTime horario,
    @NotBlank String nomePaciente,
    @NotBlank String telefonePaciente, 
    @NotBlank String idade,
    @NotBlank String sexo,
    @NotBlank String cpf,
    @NotBlank String resumoClinico
) {}