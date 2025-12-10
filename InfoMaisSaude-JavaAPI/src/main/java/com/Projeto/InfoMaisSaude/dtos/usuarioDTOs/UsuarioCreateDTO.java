package com.Projeto.InfoMaisSaude.dtos.usuarioDTOs;
import com.Projeto.InfoMaisSaude.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@AllArgsConstructor
@Getter
@Setter
public class UsuarioCreateDTO {

    private String login;
    private String senha;
    private UserRole role;
    private String loginUsuarioCriador;

    
    
    
}
