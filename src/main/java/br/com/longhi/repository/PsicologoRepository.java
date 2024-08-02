package br.com.longhi.repository;

import br.com.longhi.data.Psicologo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PsicologoRepository extends JpaRepository<Psicologo, Long>, JpaSpecificationExecutor<Psicologo> {

    Psicologo findByLogin(String login);

}
