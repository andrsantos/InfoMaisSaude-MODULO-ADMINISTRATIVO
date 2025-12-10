package com.Projeto.InfoMaisSaude.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.Projeto.InfoMaisSaude.repositories.UsuariosRepository;

@Service
public class AuthenticationService implements UserDetailsService {

    @Autowired
    private UsuariosRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
      
        UserDetails user = usuarioRepository.findByLogin(username);
        
        if (user == null) {
            throw new UsernameNotFoundException("Usuário não encontrado com o login: " + username);
        }
        
        return user;
    }
}
