package com.Projeto.InfoMaisSaude.dtos.medicoDTOs;

import java.util.List;
import com.Projeto.InfoMaisSaude.entities.Medico;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MedicoResponseReadDTO {
    Long id;
    String nome;
    String especializacao;
    String telefone;
    List<AgendaReadDTO> agenda; 


    public static MedicoResponseReadDTO fromEntity(Medico medico) {
    List<AgendaReadDTO> agendaDTOs = medico.getAgenda().stream()
        .map(a -> new AgendaReadDTO(
            a.getId(),
            a.getDiaSemana(), 
            a.getHorarioInicio(),
            a.getHorarioFim()
        ))
        .toList();

    return new MedicoResponseReadDTO(
        medico.getId(),
        medico.getNome(),
        medico.getEspecializacao(),
        medico.getTelefone(),
        agendaDTOs
    );
}
    
}
