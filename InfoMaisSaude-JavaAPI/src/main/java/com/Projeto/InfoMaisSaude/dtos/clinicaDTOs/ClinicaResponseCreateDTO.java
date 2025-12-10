package com.Projeto.InfoMaisSaude.dtos.clinicaDTOs;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ClinicaResponseCreateDTO {

    private String nomeDaClinica;
    private String mensagemDeResposta;
    private Long idDaClinica;

    
}
