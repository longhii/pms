package br.com.longhi.repository;

import br.com.longhi.data.Consulta;
import br.com.longhi.data.Psicologo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ConsultaRepository extends JpaRepository<Consulta, Long> {

    @Query("SELECT c FROM Consulta c WHERE c.paciente.psicologo = :psicologo AND c.pagamento IS NULL ORDER BY c.data DESC")
    List<Consulta> findDisponiveisPorPsicologo(@Param("psicologo") Psicologo psicologo);

    List<Consulta> findByPagamentoId(Long pagamentoId);
}
