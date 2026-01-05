package com.Projeto.InfoMaisSaude.services;

import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.Projeto.InfoMaisSaude.dtos.agendamentoDTOs.AgendamentoRequestDTO;
import com.Projeto.InfoMaisSaude.dtos.agendamentoDTOs.AgendamentoResponseDTO;
import com.Projeto.InfoMaisSaude.dtos.agendamentoDTOs.SlotDisponivelDTO;
import com.Projeto.InfoMaisSaude.dtos.consultaDTOs.ConsultaAgendadaDTO;
import com.Projeto.InfoMaisSaude.dtos.consultaDTOs.ConsultaListagemDTO;
import com.Projeto.InfoMaisSaude.dtos.consultaDTOs.FinalizarConsultaDTO;

public interface AgendamentoService {
    List<LocalTime> listarHorariosDisponiveis(Long medicoId, LocalDate data);
    AgendamentoResponseDTO agendarConsulta(AgendamentoRequestDTO dto);
    List<ConsultaListagemDTO> listarConsultas(Long medicoId, LocalDate data);
    List<SlotDisponivelDTO> listarProximosHorariosLivres(String especialidade, Long clinicaId);
    List<ConsultaListagemDTO> listarConsultasPorClinica(Long clinicaId, LocalDate data);
    void cancelarConsulta(Long consultaId, String motivo, Long usuarioId) throws AccessDeniedException;
    void finalizarConsulta(Long consultaId, FinalizarConsultaDTO dto, Long medicoId) throws AccessDeniedException;
    public void cancelarConsultaViaPaciente(Long consultaId, String motivo, String telefonePaciente) throws AccessDeniedException;
    public List<ConsultaAgendadaDTO> buscarConsultasAtivasPorTelefoneEClinica(String telefone, Long clinicaId);
}