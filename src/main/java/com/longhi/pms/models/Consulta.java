package com.longhi.pms.models;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "consultas")
public class Consulta {

    enum Status { AGENDADO, ATENDIDO, CANCELADO }

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "consultas_id_seq")
    private Long id;

    @NotNull
    @ManyToOne
    private Paciente paciente;

    @NotNull
    private LocalDateTime dataConsulta;

    @NotNull
    private Status status = Status.AGENDADO;

    private LocalDateTime ultimaAlteracao;

    @NotNull
    @ManyToOne
    private Consultorio consultorio;

}
