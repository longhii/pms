package com.longhi.pms.views.psicologos.components;

import com.longhi.pms.models.Consultorio;
import com.longhi.pms.models.DiaSemana;
import com.longhi.pms.models.HorarioAtendimento;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.data.binder.Binder;
import lombok.val;

import java.time.LocalTime;
import java.util.Locale;

public class HorariosAtendimentoComponent extends Composite<VerticalLayout> {

    private TextField dataField;
    private ComboBox<DiaSemana> diaSemanaBox;
    private TimePicker[] horarios;

    public HorariosAtendimentoComponent(Consultorio consultorio) {
        val horariosAtendimento = consultorio.getHorariosAtendimento();
        this.horarios = new TimePicker[4];

        var layout = getContent();

        this.diaSemanaBox = new ComboBox<>("Dia Semana", DiaSemana.values());

        for (var i = 0; i < horarios.length; i++) {
            var tp = new TimePicker();
            tp.setLocale(new Locale("pt", "BR"));
            tp.setMin(LocalTime.of(6, 0));
            tp.setMax(LocalTime.of(22, 0));

            this.horarios[i] = tp;
            layout.add(this.horarios[i]);
        }

        var binderHorario = new Binder<>(HorarioAtendimento.class);

        binderHorario.forField(this.horarios[0])
            .bind(HorarioAtendimento::getHorarioInicioManha, HorarioAtendimento::setHorarioInicioManha);

        binderHorario.forField(this.horarios[1])
            .bind(HorarioAtendimento::getHorarioFimManha, HorarioAtendimento::setHorarioFimManha);

        binderHorario.forField(this.horarios[2])
            .bind(HorarioAtendimento::getHorarioInicioTarde, HorarioAtendimento::setHorarioInicioTarde);

        binderHorario.forField(this.horarios[3])
            .bind(HorarioAtendimento::getHorarioFimTarde, HorarioAtendimento::setHorarioFimTarde);

        this.diaSemanaBox.addValueChangeListener(d -> {
            var diaSemana = d.getValue();

            var horarioAtendimentoCadastrado = horariosAtendimento.stream()
                .filter(ha -> ha.getDiaSemana() == diaSemana)
                .findFirst();

            if (horarioAtendimentoCadastrado.isPresent()) {
                binderHorario.setBean(horarioAtendimentoCadastrado.get());
            } else {
                var novoHorario = new HorarioAtendimento();
                novoHorario.setDiaSemana(diaSemana);
                novoHorario.setConsultorio(consultorio);
                consultorio.adicionarHorarioAtendimento(novoHorario);
                binderHorario.setBean(novoHorario);
            }
        });

        layout.add(diaSemanaBox);
    }

    public TextField getDataField() {
        return dataField;
    }

    public void setDataField(TextField dataField) {
        this.dataField = dataField;
    }

    public ComboBox<DiaSemana> getDiaSemanaBox() {
        return diaSemanaBox;
    }

    public void setDiaSemanaBox(ComboBox<DiaSemana> diaSemanaBox) {
        this.diaSemanaBox = diaSemanaBox;
    }

    public TimePicker[] getHorarios() {
        return horarios;
    }

    public void setHorarios(TimePicker[] horarios) {
        this.horarios = horarios;
    }
}
