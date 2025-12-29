package com.Projeto.InfoMaisSaude.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;
import com.Projeto.InfoMaisSaude.entities.Usuario;

@Repository
public interface UsuariosRepository extends JpaRepository<Usuario, Long> {
	
	UserDetails findByLogin(String login);
}
