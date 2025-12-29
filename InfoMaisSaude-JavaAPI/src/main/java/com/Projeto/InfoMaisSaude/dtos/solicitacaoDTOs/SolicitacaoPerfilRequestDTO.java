package com.Projeto.InfoMaisSaude.dtos.solicitacaoDTOs;

import jakarta.validation.constraints.NotBlank;

public record SolicitacaoPerfilRequestDTO(
    @NotBlank String nome,
    @NotBlank String especializacao,
    @NotBlank String telefone,
    @NotBlank String login 
) {}