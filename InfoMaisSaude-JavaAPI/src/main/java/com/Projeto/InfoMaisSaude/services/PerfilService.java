package com.Projeto.InfoMaisSaude.services;

import com.Projeto.InfoMaisSaude.dtos.medicoDTOs.MedicoResponseReadDTO;
import com.Projeto.InfoMaisSaude.dtos.perfilDTOs.medicoPerfilDTOs.MedicoPerfilDTO;
import com.Projeto.InfoMaisSaude.dtos.usuarioDTOs.UsuarioResponseReadDTO;

public interface PerfilService {

    MedicoPerfilDTO montarPerfilMedico(UsuarioResponseReadDTO usuario, MedicoResponseReadDTO medico);
    
}
