package com.Projeto.InfoMaisSaude.dtos.clinicaDTOs;

import java.time.LocalTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClinicaResponseReadDTO {
    
    private Long id;
    private String nome;
    private String cnpj;
    private String email;
    private String telefone;
    private String endereco;
    private String site;
    private LocalTime horarioFuncionamentoInicio;
    private LocalTime horarioFuncionamentoFinal;
    private Double latitude;
    private Double longitude;
    private List<String> especializacoes;

    
    
}
