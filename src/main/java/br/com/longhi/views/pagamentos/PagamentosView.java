package br.com.longhi.views.pagamentos;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import br.com.longhi.data.Consulta;
import br.com.longhi.data.Pagamento;
import br.com.longhi.data.StatusPagamento;
import br.com.longhi.repository.PacienteRepository;
import br.com.longhi.security.AuthenticatedUser;
import br.com.longhi.services.PagamentoService;
import br.com.longhi.views.MainLayout;
import jakarta.annotation.security.PermitAll;

@PageTitle("Pagamentos")
@Route(value = "pagamentos", layout = MainLayout.class)
@PermitAll
@Uses(Icon.class)
public class PagamentosView extends Composite<VerticalLayout> {

    private final PagamentoService pagamentoService;
    private final PagamentoDialog pagamentoDialog;
    private TreeGrid<Object> pagamentoTreeGrid;

    private DatePicker dataInicioFilter;
    private DatePicker dataFimFilter;
    private Checkbox emAbertoFilter;
    private Checkbox pagoFilter;
    private TextField searchField;

    @Autowired
    public PagamentosView(PagamentoService pagamentoService,
                          PacienteRepository pacienteRepository,
                          AuthenticatedUser authenticatedUser) {
        this.pagamentoService = pagamentoService;
        this.pagamentoDialog = new PagamentoDialog(pagamentoService, pacienteRepository, authenticatedUser);

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

    private void criarDialogAdicionarPagamento() {
        pagamentoDialog.abrirNovo(this::carregarDados);
    }

    private void criarDialogEditarPagamento(Pagamento pagamento) {
        pagamentoDialog.abrirEditar(pagamento, this::carregarDados);
    }
}
