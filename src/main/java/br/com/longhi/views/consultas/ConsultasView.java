package br.com.longhi.views.consultas;

import br.com.longhi.data.Consulta;
import br.com.longhi.data.Paciente;
import br.com.longhi.data.Status;
import br.com.longhi.data.StatusPagamento;
import br.com.longhi.repository.ConsultaRepository;
import br.com.longhi.repository.PacienteRepository;
import br.com.longhi.security.AuthenticatedUser;
import br.com.longhi.services.PagamentoService;
import br.com.longhi.views.MainLayout;
import br.com.longhi.views.pagamentos.PagamentoDialog;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import elemental.json.Json;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.stefan.fullcalendar.Entry;
import org.vaadin.stefan.fullcalendar.FullCalendar;
import org.vaadin.stefan.fullcalendar.FullCalendarBuilder;
import org.vaadin.stefan.fullcalendar.Timezone;
import org.vaadin.stefan.fullcalendar.dataprovider.EntryProvider;

import java.time.LocalDate;
import java.time.ZoneId;

@PageTitle("Consultas")
@Route(value = "", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@PermitAll
@Uses(Icon.class)
public class ConsultasView extends VerticalLayout {

    private final ConsultaRepository consultaRepository;
    private final PacienteRepository pacienteRepository;
    private final PagamentoService pagamentoService;
    private final PagamentoDialog pagamentoDialog;
    private final AuthenticatedUser authenticatedUser;
    private FullCalendar calendar;
    private EntryProvider<Entry> entryProvider;

    @Autowired
    public ConsultasView(ConsultaRepository consultaRepository,
                         PacienteRepository pacienteRepository,
                         PagamentoService pagamentoService,
                         AuthenticatedUser authenticatedUser) {
        this.consultaRepository = consultaRepository;
        this.pacienteRepository = pacienteRepository;
        this.pagamentoService = pagamentoService;
        this.pagamentoDialog = new PagamentoDialog(pagamentoService, pacienteRepository, authenticatedUser);
        this.authenticatedUser = authenticatedUser;

        setSizeFull();
        setPadding(true);
        setSpacing(true);

        var initialOptions = Json.createObject();
        initialOptions.put("initialView", "listMonth");
        initialOptions.put("locale", "pt-br");
        initialOptions.put("firstDay", 0);
        initialOptions.put("height", "auto");

        var headerToolbar = Json.createObject();
        headerToolbar.put("start", "");
        headerToolbar.put("center", "");
        headerToolbar.put("end", "");
        initialOptions.put("headerToolbar", headerToolbar);

        var viewsOpts = Json.createObject();
        var listDayFormat = Json.createObject();
        listDayFormat.put("month", "long");
        listDayFormat.put("day", "numeric");
        listDayFormat.put("year", "numeric");
        var listDaySideFormat = Json.createObject();
        listDaySideFormat.put("weekday", "long");
        for (var view : new String[]{"listMonth", "listWeek", "listDay"}) {
            var viewOpts = Json.createObject();
            viewOpts.put("listDayFormat", listDayFormat);
            viewOpts.put("listDaySideFormat", listDaySideFormat);
            viewsOpts.put(view, viewOpts);
        }
        initialOptions.put("views", viewsOpts);

        entryProvider = EntryProvider.fromCallbacks(
                query -> {
                    var psi = authenticatedUser.carregarPsicologoLogado();
                    return consultaRepository.findByPacientePsicologoAndDataBetween(
                            psi,
                            query.getStart().toLocalDate(),
                            query.getEnd().toLocalDate()
                    ).stream().map(Consulta::toEntry);
                },
                entryId -> consultaRepository
                        .findById(Long.valueOf(entryId))
                        .map(Consulta::toEntry)
                        .orElse(null));

        calendar = FullCalendarBuilder.create()
                .withInitialOptions(initialOptions)
                .withEntryProvider(entryProvider)
                .build();

        calendar.setTimezone(new Timezone(ZoneId.of("America/Sao_Paulo")));

        calendar.addEntryClickedListener(event -> {
            var consulta = consultaRepository.findById(
                    Long.valueOf(event.getEntry().getId())).orElse(null);
            if (consulta != null) {
                abrirDialogEditar(consulta);
            }
        });

        calendar.addTimeslotClickedListener(event -> {
            var data = event.getDate();
            if (data != null) {
                abrirDialogCriar(data);
            }
        });

        CalendarViewToolbar toolbar = new CalendarViewToolbar(calendar);

        var addButton = new Button("Nova consulta", VaadinIcon.PLUS.create());
        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addButton.addClickListener(e -> abrirDialogCriar(LocalDate.now()));

        var header = new VerticalLayout(toolbar, addButton);
        header.setPadding(false);
        header.setSpacing(true);
        header.setWidthFull();

        add(header, calendar);
        setFlexGrow(1, calendar);
        setHorizontalComponentAlignment(Alignment.STRETCH, calendar);
    }

    private void abrirDialogCriar(LocalDate dataSugerida) {
        var dialog = new Dialog();
        dialog.setHeaderTitle("Nova consulta");
        dialog.setWidth("500px");

        var binder = new Binder<>(Consulta.class);

        var pacienteField = new ComboBox<Paciente>("Paciente");
        pacienteField.setItems(pacienteRepository.findByPsicologo(
                authenticatedUser.carregarPsicologoLogado()));
        pacienteField.setItemLabelGenerator(Paciente::getNome);
        pacienteField.setRequired(true);
        binder.forField(pacienteField)
                .asRequired("Selecione um paciente")
                .bind(Consulta::getPaciente, Consulta::setPaciente);

        var dataField = new DatePicker("Data");
        dataField.setValue(dataSugerida);
        dataField.setRequired(true);
        binder.forField(dataField)
                .asRequired("Selecione uma data")
                .bind(Consulta::getData, Consulta::setData);

        var horaInicioField = new TimePicker("Hora in\u00edcio");
        horaInicioField.setRequired(true);
        binder.forField(horaInicioField)
                .asRequired("Informe o hor\u00e1rio de in\u00edcio")
                .bind(Consulta::getHoraInicio, Consulta::setHoraInicio);

        var horaFimField = new TimePicker("Hora fim");
        horaFimField.setRequired(true);
        binder.forField(horaFimField)
                .asRequired("Informe o hor\u00e1rio de t\u00e9rmino")
                .bind(Consulta::getHoraFim, Consulta::setHoraFim);

        var statusField = new Select<Status>();
        statusField.setLabel("Status");
        statusField.setItems(Status.values());
        statusField.setItemLabelGenerator(s -> {
            return switch (s) {
                case ATENDIDO -> "Atendido";
                case CANCELADO -> "Cancelado";
                case EM_ESPERA -> "Em espera";
            };
        });
        statusField.setValue(Status.EM_ESPERA);
        binder.forField(statusField)
                .asRequired("Selecione um status")
                .bind(Consulta::getStatus, Consulta::setStatus);

        var consulta = new Consulta();
        binder.setBean(consulta);

        var form = new FormLayout(pacienteField, dataField, horaInicioField, horaFimField, statusField);

        var salvarButton = new Button("Salvar", e -> {
            if (binder.validate().isOk()) {
                try {
                    consultaRepository.save(consulta);
                    entryProvider.refreshAll();
                    Notification.show("Consulta salva com sucesso.");
                    dialog.close();
                } catch (Exception ex) {
                    Notification.show("Erro ao salvar consulta: " + ex.getMessage());
                }
            } else {
                Notification.show("Verifique os campos obrigatórios.");
            }
        });
        salvarButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        var cancelarButton = new Button("Cancelar", e -> dialog.close());

        dialog.add(form);
        dialog.getFooter().add(cancelarButton, salvarButton);
        dialog.open();
    }

    private void abrirDialogEditar(Consulta consulta) {
        var dialog = new Dialog();
        dialog.setHeaderTitle("Editar consulta");
        dialog.setWidth("500px");

        var binder = new Binder<>(Consulta.class);

        var pacienteField = new ComboBox<Paciente>("Paciente");
        pacienteField.setItems(pacienteRepository.findByPsicologo(
                authenticatedUser.carregarPsicologoLogado()));
        pacienteField.setItemLabelGenerator(Paciente::getNome);
        pacienteField.setRequired(true);
        binder.forField(pacienteField)
                .asRequired("Selecione um paciente")
                .bind(Consulta::getPaciente, Consulta::setPaciente);

        var dataField = new DatePicker("Data");
        dataField.setRequired(true);
        binder.forField(dataField)
                .asRequired("Selecione uma data")
                .bind(Consulta::getData, Consulta::setData);

        var horaInicioField = new TimePicker("Hora in\u00edcio");
        horaInicioField.setRequired(true);
        binder.forField(horaInicioField)
                .asRequired("Informe o hor\u00e1rio de in\u00edcio")
                .bind(Consulta::getHoraInicio, Consulta::setHoraInicio);

        var horaFimField = new TimePicker("Hora fim");
        horaFimField.setRequired(true);
        binder.forField(horaFimField)
                .asRequired("Informe o hor\u00e1rio de t\u00e9rmino")
                .bind(Consulta::getHoraFim, Consulta::setHoraFim);

        var statusField = new Select<Status>();
        statusField.setLabel("Status");
        statusField.setItems(Status.values());
        statusField.setItemLabelGenerator(s -> {
            return switch (s) {
                case ATENDIDO -> "Atendido";
                case CANCELADO -> "Cancelado";
                case EM_ESPERA -> "Em espera";
            };
        });
        binder.forField(statusField)
                .asRequired("Selecione um status")
                .bind(Consulta::getStatus, Consulta::setStatus);

        var form = new FormLayout(pacienteField, dataField, horaInicioField, horaFimField, statusField);
        binder.setBean(consulta);

        var pagamentoSection = new VerticalLayout();
        pagamentoSection.setPadding(false);
        pagamentoSection.setSpacing(true);

        var isPago = consulta.getStatusPagamento() == StatusPagamento.PAGO;
        var pagamentoStatusLabel = new Span();
        pagamentoStatusLabel.getStyle().setFontWeight("bold");

        if (isPago) {
            pagamentoStatusLabel.setText("Pagamento: Pago");
            pagamentoStatusLabel.getStyle().setColor("green");
        } else {
            pagamentoStatusLabel.setText("Pagamento: Em aberto");
            pagamentoStatusLabel.getStyle().setColor("orange");
        }
        pagamentoSection.add(pagamentoStatusLabel);

        if (!isPago) {
            var criarPagamentoButton = new Button("Criar pagamento", VaadinIcon.MONEY.create());
            criarPagamentoButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL);
            criarPagamentoButton.addClickListener(ev -> {
                dialog.close();
                pagamentoDialog.abrirParaConsulta(consulta, entryProvider::refreshAll);
            });

            pagamentoSection.add(criarPagamentoButton);
        }

        var salvarButton = new Button("Salvar", e -> {
            if (binder.validate().isOk()) {
                try {
                    consultaRepository.save(binder.getBean());
                    entryProvider.refreshAll();
                    Notification.show("Consulta atualizada com sucesso.");
                    dialog.close();
                } catch (Exception ex) {
                    Notification.show("Erro ao atualizar consulta: " + ex.getMessage());
                }
            } else {
                Notification.show("Verifique os campos obrigat\u00f3rios.");
            }
        });
        salvarButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        var excluirButton = new Button("Excluir", VaadinIcon.TRASH.create(), e -> {
            var confirm = new ConfirmDialog();
            confirm.setHeader("Excluir consulta");
            confirm.setText("Tem certeza que deseja excluir esta consulta?");
            confirm.setCancelable(true);
            confirm.setCancelText("Cancelar");
            confirm.setConfirmText("Excluir");
            confirm.setConfirmButtonTheme("error primary");
            confirm.addConfirmListener(ev -> {
                try {
                    consultaRepository.delete(consulta);
                    entryProvider.refreshAll();
                    Notification.show("Consulta exclu\u00edda.");
                    dialog.close();
                } catch (Exception ex) {
                    Notification.show("Erro ao excluir consulta: " + ex.getMessage());
                }
            });
            confirm.open();
        });
        excluirButton.addThemeVariants(ButtonVariant.LUMO_ERROR);

        var cancelarButton = new Button("Cancelar", e -> dialog.close());

        dialog.add(form, pagamentoSection);
        dialog.getFooter().add(cancelarButton, excluirButton, salvarButton);
        dialog.open();
    }

}
