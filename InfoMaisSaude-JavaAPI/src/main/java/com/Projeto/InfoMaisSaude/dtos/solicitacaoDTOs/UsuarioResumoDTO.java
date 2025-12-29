package com.Projeto.InfoMaisSaude.dtos.solicitacaoDTOs;

import com.Projeto.InfoMaisSaude.entities.Usuario;

public record UsuarioResumoDTO(
    Long id,
    String login, 
    String role
) {
    public static UsuarioResumoDTO fromEntity(Usuario usuario) {
        if (usuario == null) return null;
        return new UsuarioResumoDTO(
            usuario.getId(),
            usuario.getLogin(), 
            usuario.getRole().name()
        );
    }
}