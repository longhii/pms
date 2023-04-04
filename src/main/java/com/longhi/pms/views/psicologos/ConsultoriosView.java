package com.longhi.pms.views.psicologos;

import com.longhi.pms.models.Consultorio;
import com.longhi.pms.services.ConsultorioService;
import com.longhi.pms.views.psicologos.components.HorariosAtendimentoComponent;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.security.RolesAllowed;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RolesAllowed("ROLE_ADMIN")
@Component
@Lazy
public class ConsultoriosView extends VerticalLayout {

    private Grid<Consultorio> consultoriosGrid;

    private Button adicionarConsultorioButton;

    private ConsultorioService consultorioService;

    public ConsultoriosView(ConsultorioService service) {
        this.consultorioService = service;
        this.adicionarConsultorioButton = new Button(new Icon(VaadinIcon.PLUS));
        this.adicionarConsultorioButton.addClickListener(ev -> criarDialogEdicaoConsultorio(new Consultorio()).open());
        criarGrid();
        add(this.adicionarConsultorioButton);
        add(consultoriosGrid);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        atualizarGrid();
    }

    private void criarGrid() {
        consultoriosGrid = new Grid<>(Consultorio.class, false);
        consultoriosGrid.addColumn(Consultorio::getEndereco).setHeader("Endereço");
        consultoriosGrid.addColumn(Consultorio::getNumero).setHeader("Número");
        consultoriosGrid.addColumn(Consultorio::getCidade).setHeader("Cidade");
        consultoriosGrid.addColumn(Consultorio::getUf).setHeader("UF");
        consultoriosGrid.addComponentColumn(consultorio -> {
            var horizontalLayout = new HorizontalLayout();

            var deleteConsultorioButton = new Button(new Icon(VaadinIcon.CLOSE_SMALL));
            deleteConsultorioButton.addClickListener(ev -> {
                consultorioService.removerConsultorio(consultorio);
                atualizarGrid();
                Notification.show("Consultório removido.");
            });

            var editarConsultorioButton = new Button(new Icon(VaadinIcon.EDIT));
            editarConsultorioButton.addClickListener(ev -> {
                criarDialogEdicaoConsultorio(consultorio).open();
                atualizarGrid();
            });

            horizontalLayout.add(deleteConsultorioButton, editarConsultorioButton);
            horizontalLayout.setSpacing(false);
            return horizontalLayout;
        }).setHeader("Ações");

        atualizarGrid();

        consultoriosGrid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
    }

    private void atualizarGrid() {
        consultoriosGrid.setItems(consultorioService.carregarConsultorios());
    }

    private Dialog criarDialogEdicaoConsultorio(Consultorio consultorioBean) {
        var dialog = new Dialog();
        dialog.setResizable(true);
        var detalhesTab = new Tab("Detalhes");
        var horariosTab = new Tab("Horário Atendimento");

        var tabs = new Tabs(detalhesTab, horariosTab);
        dialog.add(tabs);

        var formDetalhes = new FormLayout();
        detalhesTab.add(formDetalhes);

        var binderConsultorio = new Binder<Consultorio>();
        binderConsultorio.setBean(consultorioBean);

        var formHorarioAtendimento = new HorariosAtendimentoComponent(consultorioBean);
        formHorarioAtendimento.setVisible(false);
        horariosTab.add(formHorarioAtendimento);

        var formPrincipal = new Div(formDetalhes, formHorarioAtendimento);

        var forms = Map.of(0, formDetalhes, 1, formHorarioAtendimento);

        Set<com.vaadin.flow.component.Component> formsExibidos = Stream.of(formDetalhes).collect(Collectors.toSet());

        tabs.addSelectedChangeListener(ev -> {
            formsExibidos.forEach(page -> page.setVisible(false));
            formsExibidos.clear();
            var selectedPage = forms.get(tabs.getSelectedIndex());
            selectedPage.setVisible(true);
            formsExibidos.add(selectedPage);
        });

        dialog.add(formPrincipal);

        var enderecoField = new TextField();
        enderecoField.setLabel("Endereço");
        formDetalhes.add(enderecoField);

        binderConsultorio.forField(enderecoField)
            .asRequired("Necessário informar um endereço.")
            .bind(Consultorio::getEndereco, Consultorio::setEndereco);

        var numeroField = new TextField();
        numeroField.setLabel("Número");
        formDetalhes.add(numeroField);

        binderConsultorio.forField(numeroField)
            .asRequired("Necessário informar um número.")
            .bind(Consultorio::getNumero, Consultorio::setNumero);

        var cidadeField = new TextField();
        cidadeField.setLabel("Cidade");
        formDetalhes.add(cidadeField);

        binderConsultorio.forField(cidadeField)
            .asRequired("Necessário informar uma cidade.")
            .bind(Consultorio::getCidade, Consultorio::setCidade);

        var ufField = new TextField();
        ufField.setLabel("UF");
        formDetalhes.add(ufField);

        binderConsultorio.forField(ufField)
            .asRequired("Necessário informar uma UF.")
            .bind(Consultorio::getUf, Consultorio::setUf);

//        div1.setResponsiveSteps(
//            // Use one column by default
//            new FormLayout.ResponsiveStep("0", 1),
//            // Use two columns, if layout's width exceeds 500px
//            new FormLayout.ResponsiveStep("500px", 2)
//        );
//        // Stretch the username field over 2 columns
//        form.setColspan(enderecoField, 2);

        var footerLayout = new HorizontalLayout();
        dialog.add(footerLayout);

        var saveButton = new Button("Salvar");
        saveButton.addClickListener(ev -> {
            if (binderConsultorio.validate().isOk()) {
                try {
                    binderConsultorio.writeBean(consultorioBean);
                    consultorioService.inserirConsultorio(consultorioBean);
                    Notification.show("Consultório salvo com sucesso.");
                    atualizarGrid();
                    dialog.close();
                } catch (ValidationException e) {
                    throw new RuntimeException(e);
                }
            } else {
                Notification.show("Erro: " + binderConsultorio.validate().getValidationErrors());
            }
        });
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        var cancelButton = new Button("Cancel", e -> dialog.close());
        footerLayout.add(cancelButton);
        footerLayout.add(saveButton);
        footerLayout.setJustifyContentMode(JustifyContentMode.EVENLY);
        return dialog;
    }

    private Dialog criarDialogTeste() {
        Dialog dialog = new Dialog();

        Tab tab1 = new Tab("Tab one");
        Div page1 = new Div();
        page1.setText("Page#1");

        Tab tab2 = new Tab("Tab two");
        Div page2 = new Div();
        page2.setText("Page#2");
        page2.setVisible(false);

        Tab tab3 = new Tab("Tab three");
        Div page3 = new Div();
        page3.setText("Page#3");
        page3.setVisible(false);

        Map<Integer, com.vaadin.flow.component.Component> tabsToPages = new HashMap<>();
        tabsToPages.put(0, page1);
        tabsToPages.put(1, page2);
        tabsToPages.put(2, page3);
        Tabs tabs = new Tabs(tab1, tab2, tab3);
        Div pages = new Div(page1, page2, page3);
        Set<com.vaadin.flow.component.Component> pagesShown = Stream.of(page1)
            .collect(Collectors.toSet());

        tabs.addSelectedChangeListener(event -> {
            pagesShown.forEach(page -> page.setVisible(false));
            pagesShown.clear();
            com.vaadin.flow.component.Component selectedPage = tabsToPages.get(tabs.getSelectedIndex());
            selectedPage.setVisible(true);
            pagesShown.add(selectedPage);
        });

        dialog.add(tabs);
        dialog.add(pages);
        dialog.setOpened(true);
        return dialog;
    }
}
