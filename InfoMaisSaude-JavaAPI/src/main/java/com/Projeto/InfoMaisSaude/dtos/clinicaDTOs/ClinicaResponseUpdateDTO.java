package com.Projeto.InfoMaisSaude.dtos.clinicaDTOs;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ClinicaResponseUpdateDTO {

    private String nomeDaClinica;
    private String mensagemDeResposta;
    
}
