package com.Projeto.InfoMaisSaude.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.Projeto.InfoMaisSaude.dtos.usuarioDTOs.UsuarioCreateDTO;
import com.Projeto.InfoMaisSaude.dtos.usuarioDTOs.UsuarioResponseCreateDTO;
import com.Projeto.InfoMaisSaude.dtos.usuarioDTOs.UsuarioResponseDeleteDTO;
import com.Projeto.InfoMaisSaude.dtos.usuarioDTOs.UsuarioUpdateDTO;
import com.Projeto.InfoMaisSaude.dtos.usuarioDTOs.UsuarioUpdateResponseDTO;
import com.Projeto.InfoMaisSaude.exceptions.LoginJaExisteException;
import com.Projeto.InfoMaisSaude.exceptions.PermissaoException;
import com.Projeto.InfoMaisSaude.services.UsuarioService;

import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/criar")
    public ResponseEntity<?> criarNovoUsuario( @RequestBody UsuarioCreateDTO dto) {

        try {
            String loginAdmin = dto.getLoginUsuarioCriador(); 
            System.out.println("Login admin" + loginAdmin);
            UsuarioResponseCreateDTO novoUsuario = usuarioService.criarUsuario(dto, loginAdmin);
            return ResponseEntity.status(HttpStatus.CREATED).body(novoUsuario);
        } 
        catch (LoginJaExisteException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } 
        catch (PermissaoException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } 
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ocorreu um erro inesperado.");
        }
    }
    
    @GetMapping("/listar")
    public ResponseEntity<?> listarUsuarios(){
        return ResponseEntity.ok().body(usuarioService.listarUsuarios());
    }

    @PutMapping("/atualizar/{id}")
    public ResponseEntity<?> atualizarUsuario(
            @PathVariable Long id, 
            @RequestBody @Valid UsuarioUpdateDTO dto) {
        try {
            UsuarioUpdateResponseDTO usuarioAtualizado = usuarioService.atualizarUsuario(id, dto);
            return ResponseEntity.ok(usuarioAtualizado); 
        
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ocorreu um erro inesperado na atualização.");
        }
    }

    @DeleteMapping("/deletar/{id}")
    public ResponseEntity<?> deletarUsuario(@PathVariable Long id){
        try{
        UsuarioResponseDeleteDTO usuarioDeletado = usuarioService.deletarUsuario(id);
        return ResponseEntity.ok(usuarioDeletado);
        }
        catch(UsernameNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }



    
}
