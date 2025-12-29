package com.Projeto.InfoMaisSaude.services;


import java.util.List;

import com.Projeto.InfoMaisSaude.dtos.usuarioDTOs.UsuarioCreateDTO;
import com.Projeto.InfoMaisSaude.dtos.usuarioDTOs.UsuarioResponseCreateDTO;
import com.Projeto.InfoMaisSaude.dtos.usuarioDTOs.UsuarioResponseDeleteDTO;
import com.Projeto.InfoMaisSaude.dtos.usuarioDTOs.UsuarioResponseReadDTO;
import com.Projeto.InfoMaisSaude.dtos.usuarioDTOs.UsuarioUpdateDTO;
import com.Projeto.InfoMaisSaude.dtos.usuarioDTOs.UsuarioUpdateResponseDTO;

public interface UsuarioService {

    UsuarioResponseCreateDTO criarUsuario(UsuarioCreateDTO usuario, String loginAdmin);
    List<UsuarioResponseReadDTO> listarUsuarios();
    UsuarioUpdateResponseDTO atualizarUsuario(Long id, UsuarioUpdateDTO usuario);
    UsuarioResponseDeleteDTO deletarUsuario(Long id);
    UsuarioResponseReadDTO pegarUsuario(Long id);
   
}
