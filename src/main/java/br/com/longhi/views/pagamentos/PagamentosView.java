package br.com.longhi.views.pagamentos;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import br.com.longhi.data.Consulta;
import br.com.longhi.data.Pagamento;
import br.com.longhi.data.StatusPagamento;
import br.com.longhi.services.PagamentoService;
import br.com.longhi.views.MainLayout;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.ValidationException;

@PageTitle("Pagamentos")
@Route(value = "pagamentos", layout = MainLayout.class)
@PermitAll
@Uses(Icon.class)
public class PagamentosView extends Composite<VerticalLayout> {

    private final PagamentoService pagamentoService;
    private TreeGrid<Object> pagamentoTreeGrid;
    private Binder<Pagamento> binderPagamento;
    private Grid<Consulta> consultaGrid;

    private DatePicker dataInicioFilter;
    private DatePicker dataFimFilter;
    private Checkbox emAbertoFilter;
    private Checkbox pagoFilter;
    private TextField searchField;

    @Autowired
    public PagamentosView(PagamentoService pagamentoService) {
        this.pagamentoService = pagamentoService;

        getContent().setPadding(true);
        getContent().setSpacing(true);
        getContent().setWidthFull();

        var filterForm = buildFilterForm();
        var searchBar = buildSearchBar();
        pagamentoTreeGrid = buildTreeGrid();

        getContent().add(filterForm, searchBar, pagamentoTreeGrid);
        setContentFlexGrow();

        carregarDados();
    }

    private void setContentFlexGrow() {
        getContent().setFlexGrow(1, pagamentoTreeGrid);
    }

    private FormLayout buildFilterForm() {
        dataInicioFilter = new DatePicker("Per\u00edodo inicial");
        dataInicioFilter.setWidth("min-content");

        dataFimFilter = new DatePicker("Per\u00edodo final");
        dataFimFilter.setWidth("min-content");

        emAbertoFilter = new Checkbox("Em aberto");
        pagoFilter = new Checkbox("Pago");

        var filterForm = new FormLayout();
        filterForm.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("600px", 4));
        filterForm.add(dataInicioFilter, dataFimFilter);
        var flexCheck = new FlexLayout(emAbertoFilter, pagoFilter);
        flexCheck.setAlignItems(Alignment.CENTER);
        filterForm.add(flexCheck);
        filterForm.setWidthFull();

        return filterForm;
    }

    private HorizontalLayout buildSearchBar() {
        searchField = new TextField();
        searchField.setPlaceholder("Pesquisar pagamento...");
        searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        searchField.setClearButtonVisible(true);
        searchField.setValueChangeMode(ValueChangeMode.EAGER);
        searchField.addValueChangeListener(e -> carregarDados());
        searchField.setWidthFull();

        var searchButton = new Button(VaadinIcon.SEARCH.create(), e -> carregarDados());
        var addButton = new Button(VaadinIcon.PLUS.create(), e -> criarDialogAdicionarPagamento());

        var searchBar = new HorizontalLayout(searchField, searchButton, addButton);
        searchBar.setWidthFull();
        searchBar.setPadding(false);
        searchBar.setAlignItems(Alignment.CENTER);
        searchBar.setFlexGrow(1, searchField);
        searchBar.setDefaultVerticalComponentAlignment(Alignment.CENTER);

        return searchBar;
    }

    private TreeGrid<Object> buildTreeGrid() {
        var treeGrid = new TreeGrid<>();
        treeGrid.addThemeVariants(
                GridVariant.LUMO_COMPACT,
                GridVariant.LUMO_NO_BORDER,
                GridVariant.LUMO_NO_ROW_BORDERS);
        treeGrid.setWidthFull();

        treeGrid.addHierarchyColumn(pagamento -> {
            if (pagamento instanceof Pagamento p) {
                return "Pgto #" + p.getId();
            }
            return "";
        }).setHeader("Pagamento").setFlexGrow(0).setWidth("120px");

        treeGrid.addColumn(pagamento -> {
            if (pagamento instanceof Pagamento p) {
                return p.getValor() != null ? String.format("R$ %.2f", p.getValor()) : "";
            }
            return "";
        }).setHeader("Valor").setFlexGrow(0).setWidth("120px");

        treeGrid.addColumn(pagamento -> {
            if (pagamento instanceof Pagamento p) {
                return p.getStatus();
            }
            return "";
        }).setHeader("Status").setFlexGrow(0).setWidth("120px");

        treeGrid.addColumn(pagamento -> {
            if (pagamento instanceof Pagamento p) {
                if (p.getConsultas() != null && !p.getConsultas().isEmpty()) {
                    return p.getConsultas().size() + " consulta(s)";
                }
                return "Sem consultas";
            }
            if (pagamento instanceof Consulta c) {
                return c.getPaciente() != null ? c.getPaciente().getNome() : "";
            }
            return "";
        }).setHeader("Detalhes");

        treeGrid.addColumn(pagamento -> {
            if (pagamento instanceof Pagamento p) {
                return p.getData() != null ? p.getData().toLocalDate() : "";
            }
            if (pagamento instanceof Consulta c) {
                return c.getData();
            }
            return "";
        }).setHeader("Data").setFlexGrow(0).setWidth("120px");

        treeGrid.addComponentColumn(pagamento -> {
            var actions = new HorizontalLayout();
            actions.setSpacing(false);
            actions.setMargin(false);

            if (pagamento instanceof Pagamento p) {
                var btEdit = new Button(new Icon(VaadinIcon.EDIT), e -> criarDialogEditarPagamento(p));
                var btRemove = new Button(new Icon(VaadinIcon.TRASH), e -> {
                    var dialog = new ConfirmDialog();
                    dialog.setHeader("Deletar pagamento");
                    dialog.setText("Voc\u00ea tem certeza que deseja deletar esse pagamento?");
                    dialog.setCancelable(true);
                    dialog.setCancelText("Cancelar");
                    dialog.setConfirmText("Deletar");
                    dialog.setConfirmButtonTheme("error primary");
                    dialog.addConfirmListener(event -> {
                        pagamentoService.removerPagamento(p);
                        carregarDados();
                        Notification.show("Pagamento removido com sucesso.");
                    });
                    dialog.open();
                });
                actions.add(btEdit, btRemove);
            }

            return actions;
        }).setHeader("A\u00e7\u00f5es").setFlexGrow(0).setWidth("100px");

        return treeGrid;
    }

    private void carregarDados() {
        StatusPagamento statusFiltro = null;
        if (Boolean.TRUE.equals(emAbertoFilter.getValue()) && Boolean.FALSE.equals(pagoFilter.getValue())) {
            statusFiltro = StatusPagamento.AGUARDANDO;
        } else if (Boolean.FALSE.equals(emAbertoFilter.getValue()) && Boolean.TRUE.equals(pagoFilter.getValue())) {
            statusFiltro = StatusPagamento.PAGO;
        }

        var pagamentos = pagamentoService.buscarComFiltros(
                dataInicioFilter.getValue(),
                dataFimFilter.getValue(),
                statusFiltro);

        var searchTerm = searchField.getValue().trim().toLowerCase();

        pagamentoTreeGrid.setItems(
                pagamentos.stream().map(p -> (Object) p).toList(),
                pag -> {
                    if (pag instanceof Pagamento p) {
                        var consultas = p.getConsultas();
                        if (consultas == null) return java.util.Collections.emptyList();

                        if (!searchTerm.isEmpty()) {
                            return consultas.stream()
                                    .filter(c -> c.getPaciente() != null
                                            && c.getPaciente().getNome().toLowerCase().contains(searchTerm))
                                    .map(c -> (Object) c)
                                    .toList();
                        }
                        return consultas.stream().map(c -> (Object) c).toList();
                    }
                    return java.util.Collections.emptyList();
                });

        pagamentoTreeGrid.expandRecursively(
                pagamentos.stream()
                        .filter(p -> p.getConsultas() != null && !p.getConsultas().isEmpty())
                        .map(p -> (Object) p)
                        .toList(),
                1);
    }

    private Dialog criarDialogDetalhesPagamento(String titulo, Pagamento pagamentoExistente) {
        var dialog = new Dialog();
        dialog.setHeaderTitle(titulo);
        dialog.setWidth("600px");

        this.binderPagamento = new Binder<>(Pagamento.class);

        var valorField = new NumberField("Valor");
        valorField.setPrefixComponent(new Icon(VaadinIcon.DOLLAR));
        valorField.setMin(0);
        binderPagamento.forField(valorField)
                .bind(Pagamento::getValor, Pagamento::setValor);

        var dataField = new DatePicker("Data do pagamento");
        binderPagamento.forField(dataField)
                .bind(p -> p.getData() != null ? p.getData().toLocalDate() : null,
                        (p, d) -> p.setData(d != null ? d.atStartOfDay() : null));

        var statusField = new Select<StatusPagamento>();
        statusField.setLabel("Status");
        statusField.setItems(StatusPagamento.values());
        statusField.setItemLabelGenerator(s -> s == StatusPagamento.PAGO ? "Pago" : "Em aberto");
        binderPagamento.forField(statusField)
                .bind(Pagamento::getStatus, Pagamento::setStatus);

        var formLayout = new FormLayout(valorField, dataField, statusField);

        consultaGrid = new Grid<Consulta>();
        consultaGrid.addThemeVariants(GridVariant.LUMO_COMPACT, GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_NO_ROW_BORDERS);
        consultaGrid.setSelectionMode(Grid.SelectionMode.MULTI);
        consultaGrid.addColumn(c -> c.getPaciente() != null ? c.getPaciente().getNome() : "").setHeader("Paciente");
        consultaGrid.addColumn(c -> c.getData() != null ? c.getData().toString() : "").setHeader("Data");
        consultaGrid.addColumn(c -> c.getHoraInicio() != null ? c.getHoraInicio().toString() : "").setHeader("Hora");
        consultaGrid.addColumn(c -> c.getStatus() != null ? c.getStatus().name() : "").setHeader("Status");

        var consultasDisponiveis = pagamentoService.buscarConsultasDisponiveis();
        consultaGrid.setItems(consultasDisponiveis);
        consultaGrid.setWidthFull();
        consultaGrid.setHeight("200px");

        consultaGrid.addSelectionListener(e -> {
            var valorPadrao = pagamentoService.buscarValorPadraoConsulta();
            if (valorPadrao != null && valorPadrao > 0) {
                var selecionadas = e.getAllSelectedItems().size();
                var bean = binderPagamento.getBean();
                if (bean != null && (bean.getValor() == null || bean.getValor() == 0)) {
                    bean.setValor(selecionadas * valorPadrao);
                    binderPagamento.readBean(bean);
                }
            }
        });

        var consultaSection = new VerticalLayout();
        consultaSection.setPadding(false);
        consultaSection.setSpacing(false);
        var consultaLabel = new Span("Consultas vinculadas");
        consultaLabel.getStyle().setFontWeight("bold");
        consultaSection.add(consultaLabel, consultaGrid);

        if (pagamentoExistente != null && pagamentoExistente.getConsultas() != null) {
            consultaGrid.getSelectedItems().addAll(pagamentoExistente.getConsultas());
        }

        var dialogLayout = new VerticalLayout(formLayout, consultaSection);
        dialogLayout.setPadding(false);
        dialog.add(dialogLayout);
        return dialog;
    }

    private void criarDialogAdicionarPagamento() {
        var dialog = criarDialogDetalhesPagamento("Novo Pagamento", null);
        binderPagamento.setBean(new Pagamento());

        var saveButton = new Button("Salvar", e -> {
            var status = binderPagamento.validate();
            try {
                if (status.isOk()) {
                    var selectedIds = consultaGrid.getSelectedItems().stream()
                            .map(Consulta::getId)
                            .toList();

                    if (selectedIds.isEmpty()) {
                        Notification.show("Selecione pelo menos uma consulta para vincular ao pagamento.");
                        return;
                    }

                    pagamentoService.salvarPagamento(binderPagamento.getBean(), selectedIds);
                    Notification.show("Pagamento salvo com sucesso.");
                    carregarDados();
                    dialog.close();
                } else {
                    Notification.show("Verifique os erros no formul\u00e1rio.");
                }
            } catch (ValidationException | SecurityException ex) {
                Notification.show(ex.getMessage());
            } catch (Exception ex) {
                Notification.show("Erro ao salvar pagamento: " + ex.getMessage());
            }
        });
        var cancelButton = new Button("Cancelar", e -> dialog.close());
        dialog.getFooter().add(cancelButton);
        dialog.getFooter().add(saveButton);
        dialog.open();
    }

    private void criarDialogEditarPagamento(Pagamento pagamento) {
        var dialog = criarDialogDetalhesPagamento("Editar Pagamento", pagamento);
        binderPagamento.setBean(pagamento);

        var saveButton = new Button("Salvar", e -> {
            var status = binderPagamento.validate();
            try {
                if (status.isOk()) {
                    pagamentoService.salvarPagamento(binderPagamento.getBean(), java.util.Collections.emptyList());
                    Notification.show("Pagamento atualizado com sucesso.");
                    carregarDados();
                    dialog.close();
                } else {
                    Notification.show("Verifique os erros no formul\u00e1rio.");
                }
            } catch (ValidationException | SecurityException ex) {
                Notification.show(ex.getMessage());
            } catch (Exception ex) {
                Notification.show("Erro ao atualizar pagamento: " + ex.getMessage());
            }
        });
        var cancelButton = new Button("Cancelar", e -> dialog.close());
        dialog.getFooter().add(cancelButton);
        dialog.getFooter().add(saveButton);
        dialog.open();
    }
}
