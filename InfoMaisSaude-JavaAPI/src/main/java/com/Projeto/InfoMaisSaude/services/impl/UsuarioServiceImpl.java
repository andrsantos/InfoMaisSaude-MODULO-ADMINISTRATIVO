package com.Projeto.InfoMaisSaude.services.impl;

import com.Projeto.InfoMaisSaude.dtos.usuarioDTOs.UsuarioCreateDTO;
import com.Projeto.InfoMaisSaude.dtos.usuarioDTOs.UsuarioResponseCreateDTO;
import com.Projeto.InfoMaisSaude.dtos.usuarioDTOs.UsuarioResponseDeleteDTO;
import com.Projeto.InfoMaisSaude.dtos.usuarioDTOs.UsuarioResponseReadDTO;
import com.Projeto.InfoMaisSaude.dtos.usuarioDTOs.UsuarioUpdateDTO;
import com.Projeto.InfoMaisSaude.dtos.usuarioDTOs.UsuarioUpdateResponseDTO;
import com.Projeto.InfoMaisSaude.entities.Usuario;
import com.Projeto.InfoMaisSaude.enums.UserRole;
import com.Projeto.InfoMaisSaude.exceptions.LoginJaExisteException;
import com.Projeto.InfoMaisSaude.exceptions.PermissaoException;
import com.Projeto.InfoMaisSaude.repositories.UsuariosRepository;
import com.Projeto.InfoMaisSaude.services.UsuarioService;
import jakarta.transaction.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder; 
import org.springframework.stereotype.Service;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    private UsuariosRepository usuariosRepository; 
    
    @Autowired
    private PasswordEncoder passwordEncoder; 

    @Override
    @Transactional
    public UsuarioResponseCreateDTO criarUsuario(UsuarioCreateDTO dto, String loginAdmin) {

        if (verificarSeOLoginJaExiste(dto.getLogin())) {
            throw new LoginJaExisteException("Erro: O login '" + dto.getLogin() + "' já existe no banco.");
        }
        UserDetails adminDetails = usuariosRepository.findByLogin(loginAdmin);
        if (adminDetails == null) {
             throw new UsernameNotFoundException("Usuário criador não encontrado: " + loginAdmin);
        }
        Usuario adminQueCriou = (Usuario) adminDetails;
        if (!verificarSeUsuarioCriadorEhAdministrador(adminQueCriou)) {
            throw new PermissaoException("Erro: O usuário criador não tem credenciais de administrador.");
        }
        
        Usuario usuarioSendoCadastrado = new Usuario();
        usuarioSendoCadastrado.setLogin(dto.getLogin());
        usuarioSendoCadastrado.setRole(dto.getRole());
        usuarioSendoCadastrado.setSenha(passwordEncoder.encode(dto.getSenha()));
        usuarioSendoCadastrado.setCreatedBy(adminQueCriou);

        Usuario usuarioSalvo = usuariosRepository.save(usuarioSendoCadastrado);
        
        return new UsuarioResponseCreateDTO("Usuario cadastrado com sucesso!", usuarioSalvo.getLogin()); 
    }

    @Override
    public List<UsuarioResponseReadDTO> listarUsuarios() {
    
    if(verificaSeExistemUsuariosCadastrados()){
        return converteUsuarioParaUsuarioResponseDTO(usuariosRepository.findAll());
    } else {
        return Collections.emptyList();
    }
    }

    @Override
    public UsuarioUpdateResponseDTO atualizarUsuario(Long id, UsuarioUpdateDTO usuario){

       Usuario usuarioSendoAtualizado = usuariosRepository.findById(id)
       .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com o ID: " + id));
       usuarioSendoAtualizado.setSenha(passwordEncoder.encode(usuario.getSenha()));
       Usuario usuarioSalvo = usuariosRepository.save(usuarioSendoAtualizado);
       return new UsuarioUpdateResponseDTO("Senha atualizada com sucesso!", usuarioSalvo.getLogin());


    }

    @Override 
    public UsuarioResponseDeleteDTO deletarUsuario(Long id){

        Usuario usuarioSendoDeletado = usuariosRepository.findById(id)
        .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com o ID: " + id));
         usuariosRepository.delete(usuarioSendoDeletado);
         return new UsuarioResponseDeleteDTO(usuarioSendoDeletado.getLogin(), "Usuário " + usuarioSendoDeletado.getLogin() + " deletado com sucesso!");

    }

    private boolean verificaSeExistemUsuariosCadastrados(){

        if(usuariosRepository.findAll() != null){
            return true;
        } else {
            return false;
        }
        
    }

    private List<UsuarioResponseReadDTO> converteUsuarioParaUsuarioResponseDTO(List<Usuario> usuarios){

        List<UsuarioResponseReadDTO> usuariosResponseReadDTO = new ArrayList<>();

        usuarios.forEach(usuario -> {
            if(!usuario.getLogin().equals("SuperAdmin")){
                UsuarioResponseReadDTO dto = new UsuarioResponseReadDTO(usuario.getId(),  
                usuario.getLogin(),
                usuario.getRole(),
                usuario.getDataHoraCriacao(),
                usuario.getCreatedBy().getLogin());
                usuariosResponseReadDTO.add(dto);
            }
        });

        return usuariosResponseReadDTO;
    }

    private boolean verificarSeUsuarioCriadorEhAdministrador(Usuario usuarioCriador) {
        return usuarioCriador.getRole() == UserRole.ADMIN;
    }


    private boolean verificarSeOLoginJaExiste(String login) {
        return usuariosRepository.findByLogin(login) != null;
    }


}