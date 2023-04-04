package com.longhi.pms.services;

import com.longhi.pms.repositories.UsuarioRepository;
import com.longhi.pms.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailServiceImpl implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var psicologo = usuarioRepository.findPsicologoByLogin(username);
        var paciente = usuarioRepository.findPacienteByLogin(username);

        if (psicologo.isEmpty() && paciente.isEmpty())
            throw new UsernameNotFoundException("Usuário não encontrado.");

        return new UserDetailsImpl(paciente.orElseGet(psicologo::get));
    }
}
