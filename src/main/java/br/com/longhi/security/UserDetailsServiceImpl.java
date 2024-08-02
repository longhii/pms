package br.com.longhi.security;

import br.com.longhi.data.Psicologo;
import br.com.longhi.repository.PsicologoRepository;

import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final PsicologoRepository psicologoRepository;

    public UserDetailsServiceImpl(PsicologoRepository psicologoRepository) {
        this.psicologoRepository = psicologoRepository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var psi = psicologoRepository.findByLogin(username);
        if (psi == null) {
            throw new UsernameNotFoundException("No user present with username: " + username);
        } else {
            return new org.springframework.security.core.userdetails.User(psi.getLogin(), psi.getSenha(),
                    getAuthorities(psi));
        }
    }

    private static List<GrantedAuthority> getAuthorities(Psicologo user) {
        return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }

}
