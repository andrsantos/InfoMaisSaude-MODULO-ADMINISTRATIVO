package com.Projeto.InfoMaisSaude.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.Projeto.InfoMaisSaude.dtos.medicoDTOs.MedicoResponseReadDTO;
import com.Projeto.InfoMaisSaude.dtos.perfilDTOs.medicoPerfilDTOs.MedicoPerfilDTO;
import com.Projeto.InfoMaisSaude.dtos.usuarioDTOs.UsuarioResponseReadDTO;
import com.Projeto.InfoMaisSaude.entities.Usuario;
import com.Projeto.InfoMaisSaude.services.MedicosService;
import com.Projeto.InfoMaisSaude.services.PerfilService;
import com.Projeto.InfoMaisSaude.services.UsuarioService;

@RestController
@RequestMapping("/api/perfil")
public class PerfilController {
    
    @Autowired
    MedicosService medicosService;

    @Autowired
    PerfilService perfilService;

    @Autowired
    UsuarioService usuarioService;


    @GetMapping("/medico")
    @PreAuthorize("hasRole('MEDICO')")
    public ResponseEntity<MedicoPerfilDTO> verPerfilMedico(@AuthenticationPrincipal Usuario usuarioLogado) {
        MedicoResponseReadDTO medico = medicosService.pegarMedicoPorUsuario(usuarioLogado.getId());
        UsuarioResponseReadDTO usuario = usuarioService.pegarUsuario(usuarioLogado.getId());
        MedicoPerfilDTO perfil = perfilService.montarPerfilMedico(usuario, medico);
        return ResponseEntity.ok(perfil);
    }
}
