package br.com.longhi.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.stereotype.Component;

@Component
public class LoginFailureListener implements ApplicationListener<AbstractAuthenticationFailureEvent> {

    private static Logger log = LoggerFactory.getLogger(LoginFailureListener.class);

    @Override
    public void onApplicationEvent(AbstractAuthenticationFailureEvent event) {
        var auth = event.getAuthentication();
        log.info("Usuário: " + auth.getPrincipal() + " falhou ao efetuar login.");
    }
}
