package com.Projeto.InfoMaisSaude.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.Projeto.InfoMaisSaude.entities.AgendaMedica;

@Repository
public interface AgendaMedicaRepository extends JpaRepository<AgendaMedica, Long> {
    List<AgendaMedica> findByMedicoIdAndDiaSemana(Long medicoId, Integer diaSemana);
}
