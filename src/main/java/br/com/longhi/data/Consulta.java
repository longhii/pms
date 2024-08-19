package br.com.longhi.data;

import jakarta.persistence.*;
import org.vaadin.stefan.fullcalendar.Entry;
import org.vaadin.stefan.fullcalendar.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;

@Entity
@Table(name = "consultas")
public class Consulta {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "consultas_id_seq")
    private Long id;

    @ManyToOne
    @NotNull
    private Paciente paciente;

    private StatusPagamento statusPagamento;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Status status;

    private LocalDate data;

    private LocalTime horaInicio;

    private LocalTime horaFim;

    @ManyToOne
    private Pagamento pagamento;

    public Long getId() {
        return this.id;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public StatusPagamento getStatusPagamento() {
        return statusPagamento;
    }

    public void setStatusPagamento(StatusPagamento statusPagamento) {
        this.statusPagamento = statusPagamento;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public LocalTime getHoraFim() {
        return horaFim;
    }

    public void setHoraFim(LocalTime horaFim) {
        this.horaFim = horaFim;
    }

    public Entry toEntry() {
        Entry entry = new Entry(id.toString());
        entry.setTitle(paciente.getNome());
        entry.setColor(status.getCor());

        var startZonedDateTime = data.atTime(horaInicio).atZone(ZoneId.of("America/Sao_Paulo"));
        var endZonedDateTime = data.atTime(horaFim).atZone(ZoneId.of("America/Sao_Paulo"));

        entry.setStart(startZonedDateTime.withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime());
        entry.setEnd(endZonedDateTime.withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime());

        return entry;
    }
}
