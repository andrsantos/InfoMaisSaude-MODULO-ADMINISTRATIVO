package com.Projeto.InfoMaisSaude.services;

import java.util.List;
import java.util.Set;
import com.Projeto.InfoMaisSaude.dtos.clinicaDTOs.ClinicaCreateDTO;
import com.Projeto.InfoMaisSaude.dtos.clinicaDTOs.ClinicaResponseCreateDTO;
import com.Projeto.InfoMaisSaude.dtos.clinicaDTOs.ClinicaResponseDeleteDTO;
import com.Projeto.InfoMaisSaude.dtos.clinicaDTOs.ClinicaResponseReadDTO;
import com.Projeto.InfoMaisSaude.dtos.clinicaDTOs.ClinicaResponseUpdateDTO;
import com.Projeto.InfoMaisSaude.dtos.clinicaDTOs.ClinicaResumoDTO;
import com.Projeto.InfoMaisSaude.dtos.clinicaDTOs.ClinicaUpdateDTO;

public interface ClinicaService {

    ClinicaResponseCreateDTO criarClinica(ClinicaCreateDTO dto);
    List<ClinicaResponseReadDTO> listarClinicas();
    ClinicaResponseDeleteDTO deletarClinica(Long id);
    ClinicaResponseUpdateDTO atualizarClinica(Long id, ClinicaUpdateDTO dto);
    ClinicaResponseReadDTO pegarClinica(Long id);
    List<ClinicaResumoDTO> listarClinicasResumo();
    Set<String> listarEspecialidadesClinica(Long clinicaId);

    
}
