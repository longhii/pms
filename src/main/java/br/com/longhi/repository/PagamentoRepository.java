package br.com.longhi.repository;

import br.com.longhi.data.Pagamento;
import br.com.longhi.data.Psicologo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {

    List<Pagamento> findByPsicologoOrderByDataDesc(Psicologo psicologo);
}
