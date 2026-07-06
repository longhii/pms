package br.com.longhi.services;

import br.com.longhi.data.Consulta;
import br.com.longhi.data.Pagamento;
import br.com.longhi.data.StatusPagamento;
import br.com.longhi.repository.ConsultaRepository;
import br.com.longhi.repository.PagamentoRepository;
import br.com.longhi.security.AuthenticatedUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PagamentoService {

    @Autowired
    private PagamentoRepository pagamentoRepository;

    @Autowired
    private ConsultaRepository consultaRepository;

    @Autowired
    private AuthenticatedUser authenticatedUser;

    @Transactional
    public Pagamento salvarPagamento(Pagamento pagamento, List<Long> consultaIds) {
        var psi = authenticatedUser.carregarPsicologoLogado();
        pagamento.setPsicologo(psi);

        if (pagamento.getData() == null) {
            pagamento.setData(LocalDateTime.now());
        }
        if (pagamento.getStatus() == null) {
            pagamento.setStatus(StatusPagamento.AGUARDANDO);
        }

        pagamento = pagamentoRepository.save(pagamento);

        if (consultaIds != null && !consultaIds.isEmpty()) {
            var consultas = consultaRepository.findAllById(consultaIds);
            for (var consulta : consultas) {
                consulta.setPagamento(pagamento);
                consulta.setStatusPagamento(pagamento.getStatus());
                consultaRepository.save(consulta);
            }
        }

        return pagamento;
    }

    @Transactional(readOnly = true)
    public List<Pagamento> buscarComFiltros(LocalDate dataInicio, LocalDate dataFim, StatusPagamento status) {
        var psi = authenticatedUser.carregarPsicologoLogado();
        var pagamentos = pagamentoRepository.findByPsicologoOrderByDataDesc(psi);

        pagamentos.forEach(p -> {
            if (p.getConsultas() != null) {
                p.getConsultas().size();
            }
        });

        return pagamentos.stream()
                .filter(p -> dataInicio == null || !p.getData().toLocalDate().isBefore(dataInicio))
                .filter(p -> dataFim == null || !p.getData().toLocalDate().isAfter(dataFim))
                .filter(p -> status == null || status.equals(p.getStatus()))
                .toList();
    }

    public Double buscarValorPadraoConsulta() {
        var psi = authenticatedUser.carregarPsicologoLogado();
        return psi.getValorPadraoConsulta();
    }

    @Transactional(readOnly = true)
    public List<Consulta> buscarConsultasDisponiveis() {
        var psi = authenticatedUser.carregarPsicologoLogado();
        return consultaRepository.findDisponiveisPorPsicologo(psi);
    }

    @Transactional(readOnly = true)
    public Double calcularTicketMedio() {
        var psi = authenticatedUser.carregarPsicologoLogado();
        var pagamentos = pagamentoRepository.findByPsicologoAndStatus(psi, StatusPagamento.PAGO);
        if (pagamentos.isEmpty()) return 0.0;
        return pagamentos.stream()
                .mapToDouble(p -> p.getValor() != null ? p.getValor() : 0.0)
                .average()
                .orElse(0.0);
    }

    @Transactional(readOnly = true)
    public List<Object[]> calcularTicketMedioPorPaciente() {
        var psi = authenticatedUser.carregarPsicologoLogado();
        var pagamentos = pagamentoRepository.findByPsicologoAndStatus(psi, StatusPagamento.PAGO);
        return pagamentos.stream()
                .filter(p -> p.getConsultas() != null && !p.getConsultas().isEmpty())
                .collect(java.util.stream.Collectors.groupingBy(
                        p -> p.getConsultas().get(0).getPaciente().getNome(),
                        java.util.stream.Collectors.summingDouble(p -> p.getValor() != null ? p.getValor() : 0.0)
                ))
                .entrySet().stream()
                .map(e -> new Object[]{e.getKey(), e.getValue()})
                .toList();
    }

    @Transactional
    public void removerPagamento(Pagamento pagamento) {
        if (pagamento.getConsultas() != null) {
            for (var consulta : pagamento.getConsultas()) {
                consulta.setPagamento(null);
                consulta.setStatusPagamento(null);
                consultaRepository.save(consulta);
            }
        }
        pagamentoRepository.delete(pagamento);
    }
}
