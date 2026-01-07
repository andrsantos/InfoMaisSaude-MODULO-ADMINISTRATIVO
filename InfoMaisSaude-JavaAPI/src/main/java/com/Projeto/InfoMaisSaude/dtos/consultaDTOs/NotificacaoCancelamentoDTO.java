package com.Projeto.InfoMaisSaude.dtos.consultaDTOs;

public record NotificacaoCancelamentoDTO(
    String telefone,
    String nomePaciente,
    String nomeMedico,
    String dataHorario, 
    String motivo
) {}