package com.Projeto.InfoMaisSaude.dtos.solicitacaoDTOs;

import jakarta.validation.constraints.NotBlank;

public record SolicitacaoRejeicaoRequestDTO(
    @NotBlank(message = "O motivo da rejeição é obrigatório")
    String motivo
) {}