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
import java.util.List;

@Service
public class PacienteService {

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private AuthenticatedUser authenticatedUser;

    public Paciente salvarPaciente(Paciente paciente) {
        var psi = authenticatedUser.carregarPsicologoLogado();

        try {
            paciente.setPsicologo(psi);
            paciente.setCreatedAt(LocalDate.now(ZoneId.systemDefault()));
            return pacienteRepository.save(paciente);
        } catch (DataIntegrityViolationException e) {
            throw new ValidationException("Paciente com telefone já cadastrado.");
        }
    }

    public List<Paciente> buscarPacientesPorPsicologo() {
        var psi = authenticatedUser.carregarPsicologoLogado();

        return pacienteRepository.findByPsicologo(psi);
    }

    public void removerPaciente(Paciente paciente) {
        pacienteRepository.delete(paciente);
    }
}
