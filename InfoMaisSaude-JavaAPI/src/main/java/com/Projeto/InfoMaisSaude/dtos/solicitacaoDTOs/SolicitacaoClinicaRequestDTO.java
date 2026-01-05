package com.Projeto.InfoMaisSaude.dtos.solicitacaoDTOs;

import java.time.LocalTime;
import java.util.List;

public record SolicitacaoClinicaRequestDTO(
    String nome,
    String cnpj,
    String email,
    String telefone,
    String endereco,
    String site,
    LocalTime horarioFuncionamentoInicio,
    LocalTime horarioFuncionamentoFinal,
    Double latitude,
    Double longitude,
    List<String> especializacoes 
) {}