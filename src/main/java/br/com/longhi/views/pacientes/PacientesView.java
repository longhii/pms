package br.com.longhi.views.pacientes;

import br.com.longhi.data.Paciente;
import br.com.longhi.services.PacienteService;
import br.com.longhi.views.MainLayout;
import com.vaadin.componentfactory.addons.inputmask.InputMask;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.renderer.LocalDateRenderer;
import com.vaadin.flow.data.validator.EmailValidator;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;


@PageTitle("Pacientes")
@Route(value = "pacientes", layout = MainLayout.class)
@PermitAll
@Uses(Icon.class)
public class PacientesView extends Composite<VerticalLayout> {

    Button adicionarPacienteButton;
    Grid<Paciente> pacientesGrid;
    PacienteService pacienteService;
    Binder<Paciente> binderPaciente;

    @Autowired
    public PacientesView(PacienteService pacienteService) {
        this.pacienteService = pacienteService;
        var layoutRow = new HorizontalLayout();
        adicionarPacienteButton = new Button(VaadinIcon.PLUS.create(), e -> criarDialogAdicionarPaciente());

        pacientesGrid = new Grid(Paciente.class, false);
        pacientesGrid.addColumn(Paciente::getNome).setHeader("Nome");
        pacientesGrid.addColumn(new LocalDateRenderer<>(Paciente::getCreatedAt, "dd/MM/yy"))
                .setHeader("Dt. Criação");

        pacientesGrid.addColumn(Paciente::getTelefoneMask).setHeader("Telefone");

        pacientesGrid.addComponentColumn(v -> {
            var hr = new HorizontalLayout();
            hr.setSpacing(false);
            hr.setMargin(false);
            var btEdit = new Button(new Icon(VaadinIcon.EDIT), e -> {
                criarDialogEditarPaciente(v);
            });
            var btRemove = new Button(new Icon(VaadinIcon.TRASH), e -> {
                ConfirmDialog dialog = new ConfirmDialog();
                dialog.setHeader("Deletar paciente");
                dialog.setText(
                        "Você tem certeza que deseja deletar esse paciente?");

                dialog.setCancelable(true);
                dialog.setCancelText("Cancelar");
                dialog.setConfirmText("Deletar");
                dialog.addCancelListener(event -> dialog.close());

                dialog.setConfirmButtonTheme("error primary");
                dialog.addConfirmListener(event -> {
                    pacienteService.removerPaciente(v);
                    pacientesGrid.getListDataView().removeItem(v);
                    pacientesGrid.getListDataView().refreshAll();
                    Notification.show("Paciente removido com sucesso.");
                });
                dialog.open();
            });
            hr.add(btEdit, btRemove);
            return hr;
        });
        pacientesGrid.setItems(pacienteService.buscarPacientesPorPsicologo());

        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");
        layoutRow.setWidthFull();
        getContent().setFlexGrow(1.0, layoutRow);
        layoutRow.addClassName(Gap.MEDIUM);
        layoutRow.setWidth("100%");
        layoutRow.setHeight("min-content");
        layoutRow.setAlignItems(Alignment.CENTER);
        pacientesGrid.addThemeVariants(
                GridVariant.LUMO_COMPACT,
                GridVariant.LUMO_NO_BORDER,
                GridVariant.LUMO_NO_ROW_BORDERS);
        pacientesGrid.setWidth("100%");
        pacientesGrid.getStyle().set("flex-grow", "0");
        getContent().add(layoutRow);
        layoutRow.add(adicionarPacienteButton);
        getContent().add(pacientesGrid);
    }

    private Dialog criarDialogDetalhesPaciente(String titulo) {
        var dialog = new Dialog();
        dialog.setHeaderTitle(titulo);

        this.binderPaciente = new Binder<>(Paciente.class);

        var dialogLayout = new VerticalLayout();
        var nomeTextField = new TextField("Nome");
        binderPaciente.forField(nomeTextField)
                .bind(Paciente::getNome, Paciente::setNome);

        var telefoneTextField = new TextField("Telefone");
        new InputMask("(00) 00000-0000").extend(telefoneTextField);
        binderPaciente.forField(telefoneTextField)
                .withValidator(p -> p != null && p.matches("\\(\\d{2}\\) \\d{5}-\\d{4}"), "Informe um telefone válido.")
                .bind(Paciente::getTelefone, Paciente::setTelefone);

        var emailTextField = new TextField("E-mail");
        binderPaciente.forField(emailTextField)
                .withValidator(new EmailValidator("Informe um e-mail válido", true))
                .bind(Paciente::getEmail, Paciente::setEmail);

        dialogLayout.add(nomeTextField, telefoneTextField, emailTextField);
        dialog.add(dialogLayout);
        return dialog;
    }

    private void criarDialogAdicionarPaciente() {
        var dialog = criarDialogDetalhesPaciente("Novo Paciente");
        binderPaciente.setBean(new Paciente());

        var saveButton = new Button("Salvar", e -> {
            var status = binderPaciente.validate();

            try {
                if (status.isOk()) {
                    var p = pacienteService.salvarPaciente(binderPaciente.getBean());
                    Notification.show("Cliente salvo. " + p.getId());
                    pacientesGrid.getListDataView().addItem(p);
                    pacientesGrid.getListDataView().refreshAll();
                    dialog.close();
                } else {
                    Notification.show("Não foi possível cadastrar o paciente, verifique erros informados.");
                }
            } catch (ValidationException | SecurityException ex) {
                Notification.show(ex.getMessage());
            } catch (Exception ex) {
                Notification.show("Problema desconhecido ao cadastrar paciente.");
            }
        });
        var cancelButton = new Button("Cancelar", e -> dialog.close());
        dialog.getFooter().add(cancelButton);
        dialog.getFooter().add(saveButton);

        dialog.open();
    }

    // remover duplicacao com inclusao
    private void criarDialogEditarPaciente(Paciente paciente) {
        var dialog = criarDialogDetalhesPaciente("Editar Paciente");
        binderPaciente.setBean(paciente);

        var saveButton = new Button("Salvar", e -> {
            var status = binderPaciente.validate();

            try {
                if (status.isOk()) {
                    var p = pacienteService.salvarPaciente(binderPaciente.getBean());
                    Notification.show("Cliente editado. " + p.getId());
                    pacientesGrid.getListDataView().refreshAll();
                    dialog.close();
                } else {
                    Notification.show("Não foi possível cadastrar o paciente, verifique erros informados.");
                }
            } catch (ValidationException | SecurityException ex) {
                Notification.show(ex.getMessage());
            } catch (Exception ex) {
                Notification.show("Problema desconhecido ao cadastrar paciente." + ex.getMessage());
            }
        });
        var cancelButton = new Button("Cancelar", e -> dialog.close());
        dialog.getFooter().add(cancelButton);
        dialog.getFooter().add(saveButton);

        dialog.open();
    }
}
