package com.Projeto.InfoMaisSaude.dtos.consultaDTOs;

import java.time.LocalDate;
import java.time.LocalTime;

import com.Projeto.InfoMaisSaude.entities.Consulta;

public record ConsultaListagemDTO(
    Long id,
    LocalDate data,
    LocalTime horario,
    String nomePaciente,
    String telefonePaciente,
    String nomeMedico,
    String especialidadeMedico,
    String status,
    String motivo 
) {
    public static ConsultaListagemDTO fromEntity(Consulta c) {
        return new ConsultaListagemDTO(
            c.getId(),
            c.getDataConsulta(),
            c.getHorarioInicio(),
            c.getPaciente().getNome(),
            c.getPaciente().getTelefone(),
            c.getMedico().getNome(),
            c.getMedico().getEspecializacao(),
            c.getStatus().name(),
            c.getMotivoOuQueixa()
        );
    }
}