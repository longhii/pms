package br.com.longhi.repository;

import br.com.longhi.data.Pagamento;
import br.com.longhi.data.Psicologo;
import br.com.longhi.data.StatusPagamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {

    List<Pagamento> findByPsicologoOrderByDataDesc(Psicologo psicologo);

    List<Pagamento> findByPsicologoAndStatus(Psicologo psicologo, StatusPagamento status);
}
