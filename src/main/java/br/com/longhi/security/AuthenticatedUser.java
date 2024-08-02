package br.com.longhi.security;

import br.com.longhi.data.Psicologo;
import br.com.longhi.repository.PsicologoRepository;
import com.vaadin.flow.spring.security.AuthenticationContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
public class AuthenticatedUser {

    private final PsicologoRepository psicologoRepository;
    private final AuthenticationContext authenticationContext;

    public AuthenticatedUser(AuthenticationContext authenticationContext, PsicologoRepository psi) {
        this.psicologoRepository = psi;
        this.authenticationContext = authenticationContext;
    }

    @Transactional
    public Optional<Psicologo> get() {
        return authenticationContext.getAuthenticatedUser(UserDetails.class)
                .map(userDetails -> psicologoRepository.findByLogin(userDetails.getUsername()));
    }

    public void logout() {
        authenticationContext.logout();
    }

}
