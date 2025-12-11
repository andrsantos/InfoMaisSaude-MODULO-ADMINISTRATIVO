package com.Projeto.InfoMaisSaude.dtos.medicoDTOs;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MedicoResponseDeleteDTO {
    private String nomeDoMedico;
    private String mensagemDeResposta;
    
}
