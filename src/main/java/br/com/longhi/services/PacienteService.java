package br.com.longhi.services;

import br.com.longhi.data.Paciente;
import br.com.longhi.repository.PacienteRepository;
import br.com.longhi.security.AuthenticatedUser;
import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;

@Service
public class PacienteService {

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private AuthenticatedUser authenticatedUser;

    public Paciente salvarPaciente(Paciente paciente) {
        var psi = authenticatedUser.get();

        try {
            if (psi.isPresent()) {
                var psicolog = psi.get();
                paciente.setPsicologo(psicolog);
                paciente.setCreatedAt(LocalDate.now(ZoneId.systemDefault()));
                return pacienteRepository.save(paciente);
            } else {
                throw new SecurityException("Não foi possível identificar usuário logado.");
            }
        } catch (SecurityException e) {
           throw e;
        } catch (DataIntegrityViolationException e) {
            throw new ValidationException("Paciente com telefone já cadastrado.");
        }
    }
}
