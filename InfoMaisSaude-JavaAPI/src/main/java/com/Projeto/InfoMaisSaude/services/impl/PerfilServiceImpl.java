package com.Projeto.InfoMaisSaude.services.impl;

import org.springframework.stereotype.Service;

import com.Projeto.InfoMaisSaude.dtos.usuarioDTOs.UsuarioResponseReadDTO;
import com.Projeto.InfoMaisSaude.services.PerfilService;
import com.Projeto.InfoMaisSaude.dtos.medicoDTOs.MedicoResponseReadDTO;
import com.Projeto.InfoMaisSaude.dtos.perfilDTOs.medicoPerfilDTOs.MedicoPerfilDTO;

@Service
public class PerfilServiceImpl implements PerfilService {

    @Override
    public MedicoPerfilDTO montarPerfilMedico(UsuarioResponseReadDTO usuario, MedicoResponseReadDTO medico) {

    return MedicoPerfilDTO.builder()
        .nome(medico.getNome())
        .especializacao(medico.getEspecializacao())
        .telefone(medico.getTelefone())
        .login(usuario.getLogin())
        .build();

    }

    
    
}
