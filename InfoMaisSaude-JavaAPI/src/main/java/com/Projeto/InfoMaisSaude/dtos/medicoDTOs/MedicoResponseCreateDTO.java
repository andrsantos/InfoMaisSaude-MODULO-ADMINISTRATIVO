package com.Projeto.InfoMaisSaude.dtos.medicoDTOs;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MedicoResponseCreateDTO {

    private String nomeDoMedico;
    private String mensagemDeResposta;
    
}
