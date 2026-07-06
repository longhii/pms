package br.com.longhi.repository;

import br.com.longhi.data.Consulta;
import br.com.longhi.data.Paciente;
import br.com.longhi.data.Psicologo;
import br.com.longhi.data.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ConsultaRepository extends JpaRepository<Consulta, Long> {

    @Query("SELECT c FROM Consulta c WHERE c.paciente.psicologo = :psicologo AND c.pagamento IS NULL ORDER BY c.data DESC")
    List<Consulta> findDisponiveisPorPsicologo(@Param("psicologo") Psicologo psicologo);

    @Query("SELECT c FROM Consulta c WHERE c.paciente = :paciente AND c.paciente.psicologo = :psicologo AND c.pagamento IS NULL ORDER BY c.data DESC")
    List<Consulta> findDisponiveisPorPaciente(@Param("paciente") Paciente paciente, @Param("psicologo") Psicologo psicologo);

    List<Consulta> findByPagamentoId(Long pagamentoId);

    @Query("SELECT c FROM Consulta c LEFT JOIN FETCH c.paciente p LEFT JOIN FETCH p.psicologo WHERE p.psicologo = :psicologo AND c.data BETWEEN :start AND :end")
    List<Consulta> findByPacientePsicologoAndDataBetween(
            @Param("psicologo") Psicologo psicologo, @Param("start") LocalDate data, @Param("end") LocalDate dataFim
    );

    @Query("SELECT c FROM Consulta c WHERE c.paciente = :paciente AND c.data >= :data AND (c.pagamento IS NULL OR c.statusPagamento IS NULL OR c.statusPagamento <> br.com.longhi.data.StatusPagamento.PAGO) AND c.status <> :statusCancelado ORDER BY c.data, c.horaInicio")
    List<Consulta> findFutureUnpaidByPaciente(@Param("paciente") Paciente paciente, @Param("data") LocalDate data, @Param("statusCancelado") Status statusCancelado);
}
