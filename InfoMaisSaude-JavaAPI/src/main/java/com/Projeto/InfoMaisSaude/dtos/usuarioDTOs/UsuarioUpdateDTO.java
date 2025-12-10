package com.Projeto.InfoMaisSaude.dtos.usuarioDTOs;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsuarioUpdateDTO {
    private String senha;

    public UsuarioUpdateDTO(String senha){
        this.senha = senha;
    }


    
}
