package com.Projeto.InfoMaisSaude.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.Projeto.InfoMaisSaude.entities.Medico;

@Repository
public interface MedicosRepository extends JpaRepository<Medico, Long> {

    boolean existsByTelefone(String telefone);
    Medico findByUsuarioId(Long idUsuario);
    List<Medico> findByEspecializacao(String especializacao);
    List<Medico> findByNomeContainingIgnoreCase(String nome);
    @Query("SELECT m FROM Medico m LEFT JOIN FETCH m.agenda WHERE m.id = :id")
    Optional<Medico> findByIdWithAgenda(@Param("id") Long id);
    @Query("SELECT DISTINCT m FROM Medico m LEFT JOIN FETCH m.agenda")
    List<Medico> findAllWithAgenda();
    List<Medico> findByEspecializacaoContainingIgnoreCase(String especializacao);
    List<Medico> findByClinicaId(Long clinicaId);
    List<Medico> findByClinicaIdAndEspecializacaoContainingIgnoreCase(Long clinicaId, String especializacao);

}