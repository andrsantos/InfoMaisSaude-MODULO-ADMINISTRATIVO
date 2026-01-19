package com.Projeto.InfoMaisSaude.controllers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.Projeto.InfoMaisSaude.dtos.loginDTOs.LoginRequestDTO;
import com.Projeto.InfoMaisSaude.dtos.loginDTOs.LoginResponseDTO;
import com.Projeto.InfoMaisSaude.entities.Clinica;
import com.Projeto.InfoMaisSaude.entities.Usuario;
import com.Projeto.InfoMaisSaude.enums.UserRole;
import com.Projeto.InfoMaisSaude.repositories.ClinicaRepository;
import com.Projeto.InfoMaisSaude.services.TokenService;

@RestController
@RequestMapping("/api/login")
public class AuthenticationController {

    @Autowired
    private AuthenticationManager manager;

    @Autowired
    private ClinicaRepository clinicaRepository;

    @Autowired
    private TokenService tokenService;

    @PostMapping
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO dados) {
        var authToken = new UsernamePasswordAuthenticationToken(dados.getLogin(), dados.getSenha());
        var authentication = manager.authenticate(authToken);
        Usuario usuarioAutenticado = (Usuario) authentication.getPrincipal();
        var tokenJWT = tokenService.gerarToken((Usuario) authentication.getPrincipal());
        Optional<Clinica> clinica = clinicaRepository.findByUsuarioId(usuarioAutenticado.getId());
        Long clinicaId = clinica.map(Clinica::getId).orElse(null);
        boolean possuiClinica = clinica.isPresent();

        if(usuarioAutenticado.getRole() == UserRole.CLINICA){
            possuiClinica = clinicaRepository.existsByUsuario(usuarioAutenticado);
        }

        Long idUsuario = usuarioAutenticado.getId();


        return ResponseEntity.ok(new LoginResponseDTO(tokenJWT, possuiClinica, idUsuario, clinicaId));
    }
}