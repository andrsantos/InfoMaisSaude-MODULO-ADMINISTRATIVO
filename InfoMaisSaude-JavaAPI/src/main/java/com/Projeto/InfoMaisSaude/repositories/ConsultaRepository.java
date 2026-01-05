package com.Projeto.InfoMaisSaude.repositories;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.Projeto.InfoMaisSaude.entities.Consulta;
import com.Projeto.InfoMaisSaude.enums.StatusConsulta;

@Repository
public interface ConsultaRepository extends JpaRepository<Consulta, Long> {
    
    List<Consulta> findByMedicoIdAndDataConsultaAndStatusNot(
        Long medicoId, 
        LocalDate data, 
        StatusConsulta statusCancelado
    );

    boolean existsByPacienteIdAndDataConsultaAndHorarioInicio(
        Long pacienteId, 
        LocalDate data, 
        LocalTime horario
    );

    boolean existsByMedicoIdAndDataConsultaAndHorarioInicioAndStatusIn(
        Long medicoId, 
        LocalDate data, 
        LocalTime horario, 
        List<StatusConsulta> statusOcupados
    );

    List<Consulta> findByMedicoIdAndDataConsultaOrderByHorarioInicio(Long medicoId, LocalDate data);

    List<Consulta> findByDataConsultaOrderByHorarioInicio(LocalDate data);
    
    List<Consulta> findByMedicoIdAndDataConsultaBetweenOrderByDataConsultaAscHorarioInicioAsc(
        Long medicoId, LocalDate inicio, LocalDate fim
    );

    List<Consulta> findByClinicaId(Long clinicaId);

    List<Consulta> findByClinicaIdAndDataConsultaOrderByHorarioInicioAsc(Long clinicaId, LocalDate data);
}