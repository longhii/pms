package com.longhi.pms.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "horario_atendimento")
@Getter @Setter @NoArgsConstructor
public class HorarioAtendimento {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "horario_atendimento_id_seq")
    private Long id;

    @Enumerated(EnumType.STRING)
    private DiaSemana diaSemana;

    private LocalTime horarioInicioManha;
    private LocalTime horarioFimManha;
    private LocalTime horarioInicioTarde;
    private LocalTime horarioFimTarde;

    @ManyToOne
    private Consultorio consultorio;

    public HorarioAtendimento(DiaSemana diaSemana,
                              LocalTime horarioInicioManha,
                              LocalTime horarioFimManha,
                              LocalTime horarioInicioTarde,
                              LocalTime horarioFimTarde) {
        this.diaSemana = diaSemana;
        this.horarioInicioManha = horarioInicioManha;
        this.horarioFimManha = horarioFimManha;
        this.horarioInicioTarde = horarioInicioTarde;
        this.horarioFimTarde = horarioFimTarde;
    }

    public HorarioAtendimento(DiaSemana diaSemana, List<LocalTime> horarios) {
        this.diaSemana = diaSemana;
        this.horarioInicioManha = horarios.get(0);
        this.horarioFimManha = horarios.get(1);
        this.horarioInicioTarde = horarios.get(2);
        this.horarioFimTarde = horarios.get(3);
    }
}
