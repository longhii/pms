package com.longhi.pms.models;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "pagamentos")
public class Pagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pagamentos_id_seq")
    private Long id;

    private Double valor;

    private LocalDateTime dataPagamento;

    @ManyToOne
    private Paciente paciente;
}
