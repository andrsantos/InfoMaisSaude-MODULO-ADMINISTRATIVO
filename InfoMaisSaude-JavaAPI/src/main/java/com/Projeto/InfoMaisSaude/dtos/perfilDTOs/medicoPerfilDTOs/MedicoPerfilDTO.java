package com.Projeto.InfoMaisSaude.dtos.perfilDTOs.medicoPerfilDTOs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MedicoPerfilDTO {

    String nome;
    String especializacao;
    String telefone;
    String login;


    
}
