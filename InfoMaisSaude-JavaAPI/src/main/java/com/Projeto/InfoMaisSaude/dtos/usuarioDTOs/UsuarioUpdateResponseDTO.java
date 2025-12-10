package com.Projeto.InfoMaisSaude.dtos.usuarioDTOs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UsuarioUpdateResponseDTO {
    
    private String login;
    private String mensagemDeResposta;
    
}
