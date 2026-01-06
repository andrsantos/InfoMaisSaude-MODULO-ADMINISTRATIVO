package com.Projeto.InfoMaisSaude.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.Projeto.InfoMaisSaude.entities.Clinica;
import com.Projeto.InfoMaisSaude.entities.Usuario;


@Repository
public interface ClinicaRepository extends JpaRepository<Clinica, Long>{
    Clinica findByLatitudeAndLongitude(Double latitude, Double longitude);
    boolean existsByUsuario(Usuario usuario);
    Optional<Clinica> findByUsuarioId(Long id);
    
}
