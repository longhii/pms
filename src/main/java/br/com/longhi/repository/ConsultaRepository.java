package br.com.longhi.repository;

import br.com.longhi.data.Consulta;
import br.com.longhi.data.Psicologo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ConsultaRepository extends JpaRepository<Consulta, Long> {

    @Query("SELECT c FROM Consulta c LEFT JOIN FETCH c.paciente p LEFT JOIN FETCH p.psicologo WHERE p.psicologo = :psicologo AND c.data BETWEEN :start AND :end")
    List<Consulta> findByPacientePsicologoAndDataBetween(
            @Param("psicologo") Psicologo psicologo, @Param("start") LocalDate data, @Param("end") LocalDate dataFim
    );
}
