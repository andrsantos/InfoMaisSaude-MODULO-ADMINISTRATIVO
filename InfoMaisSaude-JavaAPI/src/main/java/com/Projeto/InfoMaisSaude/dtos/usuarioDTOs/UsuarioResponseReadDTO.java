package com.Projeto.InfoMaisSaude.dtos.usuarioDTOs;

import java.time.OffsetDateTime;

import com.Projeto.InfoMaisSaude.enums.UserRole;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UsuarioResponseReadDTO {
    
    Long id;
    String login;
    UserRole tipo;
    OffsetDateTime dataHoraCriacao;
    String loginDoUsuarioCriador;


    
    
}
