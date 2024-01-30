package com.longhi.pms.views.psicologos.consultorio;

import com.longhi.pms.models.Consultorio;
import com.longhi.pms.models.DiaSemana;
import com.longhi.pms.models.HorarioAtendimento;
import com.longhi.pms.services.ConsultorioService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.binder.ValidationResult;

import java.time.LocalTime;
import java.util.Comparator;
import java.util.Locale;

public class HorariosAtendimentoView extends VerticalLayout {
    
    private Grid<HorarioAtendimento> horariosAtendimentoGrid;
    private Button adicionarHorarioAtendimentoButton;
    private Consultorio consultorio;
    private ConsultorioService consultorioService;

    public HorariosAtendimentoView(ConsultorioService service, Consultorio consultorio) {
        this.consultorioService = service;
        this.consultorio = consultorio;
        this.adicionarHorarioAtendimentoButton = new Button(new Icon(VaadinIcon.PLUS));
        this.adicionarHorarioAtendimentoButton.addClickListener(ev -> {
            criarDialogEdicaoHorarioAtendimento(consultorio, new HorarioAtendimento()).open();
        });
        criarGrid();
        add(this.adicionarHorarioAtendimentoButton);
        add(horariosAtendimentoGrid);
    }

    private void criarGrid() {
        horariosAtendimentoGrid = new Grid<>(HorarioAtendimento.class, false);
        horariosAtendimentoGrid.addColumn(HorarioAtendimento::getDiaSemana).setHeader("Dia Semana");
        horariosAtendimentoGrid.addColumn(HorarioAtendimento::getHorarioInicioManha).setHeader("Ini. Manhã");
        horariosAtendimentoGrid.addColumn(HorarioAtendimento::getHorarioFimManha).setHeader("Fim Manhã");
        horariosAtendimentoGrid.addColumn(HorarioAtendimento::getHorarioInicioTarde).setHeader("Ini. Tarde");
        horariosAtendimentoGrid.addColumn(HorarioAtendimento::getHorarioFimTarde).setHeader("Fim Tarde");
        horariosAtendimentoGrid.addComponentColumn(horarioAtendimento -> {
            var horizontalLayout = new HorizontalLayout();

            var deleteHorarioAtendimento = new Button(new Icon(VaadinIcon.CLOSE_SMALL));
            deleteHorarioAtendimento.addClickListener(ev -> {
                consultorioService.removerHorarioAtendimento(consultorio, horarioAtendimento);
                atualizarGrid();
                Notification.show("Horário atendimento removido.");
            });

            var editarHorarioAtendimento = new Button(new Icon(VaadinIcon.EDIT));
            editarHorarioAtendimento.addClickListener(ev -> {
                criarDialogEdicaoHorarioAtendimento(consultorio, horarioAtendimento).open();
                atualizarGrid();
            });

            horizontalLayout.add(deleteHorarioAtendimento, editarHorarioAtendimento);
            horizontalLayout.setSpacing(false);
            return horizontalLayout;
        }).setHeader("Ações");

        atualizarGrid();

        horariosAtendimentoGrid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
    }

    private void atualizarGrid() {
        var hrs = consultorio.getHorariosAtendimento().stream()
                .sorted(Comparator.comparingInt((HorarioAtendimento c) -> c.getDiaSemana().ordinal()));
        horariosAtendimentoGrid.setItems(hrs);
    }

    private Dialog criarDialogEdicaoHorarioAtendimento(Consultorio consultorio, HorarioAtendimento horarioAtendimento) {
        var binderHorario = new Binder<>(HorarioAtendimento.class);
        binderHorario.setBean(horarioAtendimento);

        var dialog = new Dialog();
        dialog.setResizable(true);

        var horarios = new TimePicker[4];

        // var diaSemanaBox = new ComboBox<>("Dia Semana", DiaSemana.values());
        var diaSemanaBox = new ComboBox<>("Dia Semana");
        diaSemanaBox.setItems(DiaSemana.values());
        diaSemanaBox.setValue(horarioAtendimento.getDiaSemana());
        diaSemanaBox.setOpened(false);
        diaSemanaBox.setRequired(true);
        dialog.add(diaSemanaBox);

        for (var i = 0; i < horarios.length; i++) {
            var tp = new TimePicker();
            tp.setLocale(new Locale("pt", "BR"));
            tp.setMin(LocalTime.of(6, 0));
            tp.setMax(LocalTime.of(22, 0));

            horarios[i] = tp;
            dialog.add(horarios[i]);
        }

        var horariosAtendimento = consultorio.getHorariosAtendimento();

        diaSemanaBox.addValueChangeListener(d -> {
            var diaSemana = (DiaSemana) d.getValue();

            var horarioAtendimentoCadastrado = horariosAtendimento.stream()
                    .filter(ha -> ha.getDiaSemana() == diaSemana)
                    .findFirst();

            if (horarioAtendimentoCadastrado.isPresent()) {
                binderHorario.setBean(horarioAtendimentoCadastrado.get());
            } else {
                horarioAtendimento.setDiaSemana(diaSemana);
                horarioAtendimento.setConsultorio(consultorio);
                consultorio.adicionarHorarioAtendimento(horarioAtendimento);
            }
        });

        binderHorario.forField(horarios[0])
                .bind(HorarioAtendimento::getHorarioInicioManha, HorarioAtendimento::setHorarioInicioManha);

        binderHorario.forField(horarios[1])
                .bind(HorarioAtendimento::getHorarioFimManha, HorarioAtendimento::setHorarioFimManha);

        binderHorario.forField(horarios[2])
                .bind(HorarioAtendimento::getHorarioInicioTarde, HorarioAtendimento::setHorarioInicioTarde);

        binderHorario.forField(horarios[3])
                .bind(HorarioAtendimento::getHorarioFimTarde, HorarioAtendimento::setHorarioFimTarde);

        binderHorario.withValidator((h, c) -> {
            var horarioMatutinoValido = (horarios[0].getValue() != null && horarios[1].getValue() != null);
            var horarioVespertinoValido = (horarios[2].getValue() != null && horarios[3].getValue() != null);

            if (horarioVespertinoValido || horarioMatutinoValido) {
                return ValidationResult.ok();
            } else {
                var message = "Necessário informar período matutino ou vespertino";
                return ValidationResult.error(message);
            }
        });

        var footerLayout = new HorizontalLayout();
        dialog.add(footerLayout);

        var saveButton = new Button("Salvar");
        saveButton.addClickListener(ev -> {
            var validacaoHorarios = binderHorario.validate();

            if (validacaoHorarios.isOk()) {
                    // binderHorario.writeBean(horarioAtendimento);
                    //consultorio.setHorariosAtendimento(horariosAtendimento);
                    // consultorioService.inserirConsultorio(consultorio);
                    atualizarGrid();
                    dialog.close();

            } else {
                validacaoHorarios.getValidationErrors()
                        .forEach(v -> Notification.show("Erro: " + v.getErrorMessage()));
            }
        });

        var cancelButton = new Button("Cancel", e -> dialog.close());

        footerLayout.add(saveButton);
        footerLayout.add(cancelButton);
        footerLayout.setJustifyContentMode(JustifyContentMode.EVENLY);

        return dialog;
    }
}
