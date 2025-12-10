package com.Projeto.InfoMaisSaude.dtos.usuarioDTOs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UsuarioResponseCreateDTO {
    private String mensagemDeResposta;
    private String loginDoUsuarioCriado;

    
}
