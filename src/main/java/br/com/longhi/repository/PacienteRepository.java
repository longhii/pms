package br.com.longhi.repository;

import br.com.longhi.data.Paciente;
import br.com.longhi.data.Psicologo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PacienteRepository extends JpaRepository<Paciente, Long> {

    List<Paciente> findByPsicologo(Psicologo psi);

}
