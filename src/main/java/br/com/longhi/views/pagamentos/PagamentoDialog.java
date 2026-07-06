package br.com.longhi.views.pagamentos;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.data.binder.Binder;

import br.com.longhi.data.Consulta;
import br.com.longhi.data.Paciente;
import br.com.longhi.data.Pagamento;
import br.com.longhi.data.StatusPagamento;
import br.com.longhi.repository.PacienteRepository;
import br.com.longhi.security.AuthenticatedUser;
import br.com.longhi.services.PagamentoService;
import jakarta.validation.ValidationException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;

public class PagamentoDialog {

    private final PagamentoService pagamentoService;
    private final PacienteRepository pacienteRepository;
    private final AuthenticatedUser authenticatedUser;

    private final Dialog dialog;
    private final ComboBox<Paciente> pacienteField;
    private final NumberField valorField;
    private final DatePicker dataField;
    private final Select<StatusPagamento> statusField;
    private final Grid<Consulta> consultaGrid;
    private final Binder<Pagamento> binder;

    private Runnable onSave;
    private boolean editando;

    public PagamentoDialog(PagamentoService pagamentoService,
                           PacienteRepository pacienteRepository,
                           AuthenticatedUser authenticatedUser) {
        this.pagamentoService = pagamentoService;
        this.pacienteRepository = pacienteRepository;
        this.authenticatedUser = authenticatedUser;

        dialog = new Dialog();
        dialog.setWidth("600px");

        binder = new Binder<>(Pagamento.class);

        valorField = new NumberField("Valor");
        valorField.setPrefixComponent(new Icon(VaadinIcon.DOLLAR));
        valorField.setMin(0);
        valorField.setPlaceholder("0,00");
        binder.forField(valorField)
                .bind(Pagamento::getValor, Pagamento::setValor);

        dataField = new DatePicker("Data do pagamento");
        binder.forField(dataField)
                .bind(p -> p.getData() != null ? p.getData().toLocalDate() : null,
                        (p, d) -> p.setData(d != null ? d.atStartOfDay() : null));

        statusField = new Select<StatusPagamento>();
        statusField.setLabel("Status");
        statusField.setItems(StatusPagamento.values());
        statusField.setItemLabelGenerator(s -> s == StatusPagamento.PAGO ? "Pago" : "Em aberto");
        statusField.setValue(StatusPagamento.PAGO);
        binder.forField(statusField)
                .bind(Pagamento::getStatus, Pagamento::setStatus);

        consultaGrid = new Grid<Consulta>();
        consultaGrid.addThemeVariants(GridVariant.LUMO_COMPACT, GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_NO_ROW_BORDERS);
        consultaGrid.setSelectionMode(Grid.SelectionMode.MULTI);
        consultaGrid.addColumn(c -> c.getPaciente() != null ? c.getPaciente().getNome() : "").setHeader("Paciente");
        consultaGrid.addColumn(c -> c.getData() != null ? c.getData().toString() : "").setHeader("Data");
        consultaGrid.addColumn(c -> c.getHoraInicio() != null ? c.getHoraInicio().toString() : "").setHeader("Hora");
        consultaGrid.addColumn(c -> c.getStatus() != null ? c.getStatus().name() : "").setHeader("Status");
        consultaGrid.setWidthFull();
        consultaGrid.setHeight("200px");
        consultaGrid.addSelectionListener(e -> {
            if (!editando) {
                recalcularValor();
            }
        });

        pacienteField = new ComboBox<>("Paciente");
        pacienteField.setItemLabelGenerator(Paciente::getNome);
        pacienteField.setRequiredIndicatorVisible(true);
        pacienteField.setClearButtonVisible(true);
        pacienteField.addValueChangeListener(e -> {
            if (e.getValue() != null) {
                if (!consultaGrid.getSelectedItems().isEmpty()) {
                    Notification.show("Selecione apenas consultas do mesmo paciente.");
                    pacienteField.setValue(e.getOldValue());
                    return;
                }
                carregarConsultas(e.getValue());
            } else {
                consultaGrid.setItems(Collections.emptyList());
                consultaGrid.deselectAll();
            }
        });

        var formLayout = new FormLayout(pacienteField, valorField, dataField, statusField);

        var consultaSection = new VerticalLayout();
        consultaSection.setPadding(false);
        consultaSection.setSpacing(false);
        var consultaLabel = new Span("Consultas vinculadas");
        consultaLabel.getStyle().setFontWeight("bold");
        consultaSection.add(consultaLabel, consultaGrid);

        var dialogLayout = new VerticalLayout(formLayout, consultaSection);
        dialogLayout.setPadding(false);
        dialog.add(dialogLayout);

        var cancelarButton = new Button("Cancelar", e -> dialog.close());
        var salvarButton = new Button("Salvar", e -> salvar());

        dialog.getFooter().add(cancelarButton, salvarButton);
    }

    public void abrirNovo(Runnable onSaveCallback) {
        this.editando = false;
        this.onSave = onSaveCallback;

        var psi = authenticatedUser.carregarPsicologoLogado();
        pacienteField.setItems(pacienteRepository.findByPsicologo(psi));
        pacienteField.setValue(null);
        pacienteField.setEnabled(true);

        dataField.setValue(LocalDate.now());
        statusField.setValue(StatusPagamento.PAGO);
        valorField.setValue(null);

        consultaGrid.setItems(Collections.emptyList());
        consultaGrid.deselectAll();

        var pagamento = new Pagamento();
        pagamento.setData(LocalDateTime.now());
        pagamento.setStatus(StatusPagamento.PAGO);
        binder.setBean(pagamento);

        dialog.setHeaderTitle("Novo Pagamento");
        dialog.open();
    }

    public void abrirEditar(Pagamento pagamento, Runnable onSaveCallback) {
        this.editando = true;
        this.onSave = onSaveCallback;

        var psi = authenticatedUser.carregarPsicologoLogado();
        pacienteField.setItems(pacienteRepository.findByPsicologo(psi));

        if (pagamento.getConsultas() != null && !pagamento.getConsultas().isEmpty()) {
            var paciente = pagamento.getConsultas().get(0).getPaciente();
            pacienteField.setValue(paciente);
            pacienteField.setEnabled(false);
            carregarConsultas(paciente);
            consultaGrid.getListDataView().getItems()
                    .filter(c -> pagamento.getConsultas().stream()
                            .anyMatch(pc -> pc.getId().equals(c.getId())))
                    .forEach(consultaGrid::select);
        }

        dataField.setValue(pagamento.getData() != null ? pagamento.getData().toLocalDate() : LocalDate.now());
        statusField.setValue(pagamento.getStatus() != null ? pagamento.getStatus() : StatusPagamento.AGUARDANDO);
        valorField.setValue(pagamento.getValor());

        binder.setBean(pagamento);

        dialog.setHeaderTitle("Editar Pagamento");
        dialog.open();
    }

    public void abrirParaConsulta(Consulta consulta, Runnable onSaveCallback) {
        this.editando = false;
        this.onSave = onSaveCallback;

        var psi = authenticatedUser.carregarPsicologoLogado();
        pacienteField.setItems(pacienteRepository.findByPsicologo(psi));

        var paciente = consulta.getPaciente();
        pacienteField.setValue(paciente);
        pacienteField.setEnabled(false);

        dataField.setValue(LocalDate.now());
        statusField.setValue(StatusPagamento.PAGO);
        valorField.setValue(null);

        carregarConsultas(paciente);
        consultaGrid.deselectAll();
        consultaGrid.getListDataView().getItems()
                .filter(c -> c.getId().equals(consulta.getId()))
                .findFirst()
                .ifPresent(consultaGrid::select);

        var pagamento = new Pagamento();
        pagamento.setData(LocalDateTime.now());
        pagamento.setStatus(StatusPagamento.PAGO);
        binder.setBean(pagamento);

        recalcularValor();

        dialog.setHeaderTitle("Novo Pagamento");
        dialog.open();
    }

    private void carregarConsultas(Paciente paciente) {
        var consultas = pagamentoService.buscarConsultasDisponiveisPorPaciente(paciente);
        consultaGrid.setItems(consultas);
    }

    private void recalcularValor() {
        var valorPadrao = pagamentoService.buscarValorPadraoConsulta();
        if (valorPadrao != null && valorPadrao > 0) {
            var selecionadas = consultaGrid.getSelectedItems().size();
            var bean = binder.getBean();
            if (bean != null) {
                bean.setValor(selecionadas * valorPadrao);
                binder.readBean(bean);
            }
        }
    }

    private void salvar() {
        if (pacienteField.getValue() == null) {
            Notification.show("Selecione um paciente para carregar as consultas.");
            return;
        }

        var status = binder.validate();
        try {
            if (status.isOk()) {
                var selectedIds = consultaGrid.getSelectedItems().stream()
                        .map(Consulta::getId)
                        .toList();

                if (selectedIds.isEmpty()) {
                    Notification.show("Selecione pelo menos uma consulta para vincular ao pagamento.");
                    return;
                }

                pagamentoService.salvarPagamento(binder.getBean(), selectedIds);
                Notification.show(editando ? "Pagamento atualizado com sucesso." : "Pagamento salvo com sucesso.");
                if (onSave != null) {
                    onSave.run();
                }
                dialog.close();
            } else {
                Notification.show("Verifique os erros no formul\u00e1rio.");
            }
        } catch (ValidationException | SecurityException ex) {
            Notification.show(ex.getMessage());
        } catch (Exception ex) {
            Notification.show("Erro ao " + (editando ? "atualizar" : "salvar") + " pagamento: " + ex.getMessage());
        }
    }
}
