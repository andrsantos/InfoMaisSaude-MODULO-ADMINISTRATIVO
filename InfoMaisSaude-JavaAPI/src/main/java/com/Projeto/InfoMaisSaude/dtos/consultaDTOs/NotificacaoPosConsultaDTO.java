package com.Projeto.InfoMaisSaude.dtos.consultaDTOs;

public record NotificacaoPosConsultaDTO(
    String telefone,
    String nomePaciente,
    String nomeMedico,
    String prescricao
) {}