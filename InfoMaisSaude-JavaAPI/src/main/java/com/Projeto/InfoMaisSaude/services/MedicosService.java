package com.Projeto.InfoMaisSaude.services;

import java.util.List;
import com.Projeto.InfoMaisSaude.dtos.medicoDTOs.MedicoCreateDTO;
import com.Projeto.InfoMaisSaude.dtos.medicoDTOs.MedicoResponseCreateDTO;
import com.Projeto.InfoMaisSaude.dtos.medicoDTOs.MedicoResponseDeleteDTO;
import com.Projeto.InfoMaisSaude.dtos.medicoDTOs.MedicoResponseReadDTO;
import com.Projeto.InfoMaisSaude.dtos.medicoDTOs.MedicoResponseUpdateDTO;
import com.Projeto.InfoMaisSaude.dtos.medicoDTOs.MedicoUpdateDTO;

public interface MedicosService {

  MedicoResponseCreateDTO criarMedico(MedicoCreateDTO dto);
  List<MedicoResponseReadDTO> listarMedicos();
  MedicoResponseDeleteDTO deletarMedico(Long id);
  MedicoResponseUpdateDTO atualizarMedico(Long id, MedicoUpdateDTO dto);
  MedicoResponseReadDTO pegarMedico(Long id);    
  MedicoResponseReadDTO pegarMedicoPorUsuario(Long idUsuario);

}
