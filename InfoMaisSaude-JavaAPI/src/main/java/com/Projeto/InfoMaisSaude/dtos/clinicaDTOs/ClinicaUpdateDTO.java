package com.Projeto.InfoMaisSaude.dtos.clinicaDTOs;

import java.time.LocalTime;
import java.util.Set;

import com.Projeto.InfoMaisSaude.enums.Especializacao;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ClinicaUpdateDTO {

    private String nome;
    private String email;
    private String telefone;
    private String endereco;
    private String site;
    private String cnpj;
    private Set<Especializacao> especializacoes;
    private LocalTime horarioFuncionamentoInicio;
    private LocalTime horarioFuncionamentoFinal;
    private Double latitude;
    private Double longitude;
    
}
