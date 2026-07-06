package br.com.longhi.views.configuracoes;

import br.com.longhi.data.Psicologo;
import br.com.longhi.repository.PsicologoRepository;
import br.com.longhi.security.AuthenticatedUser;
import br.com.longhi.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;

@PageTitle("Configurações")
@Route(value = "configuracoes", layout = MainLayout.class)
@PermitAll
@Uses(Icon.class)
public class ConfiguracoesView extends VerticalLayout {

    @Autowired
    public ConfiguracoesView(PsicologoRepository psicologoRepository,
                             AuthenticatedUser authenticatedUser) {
        setWidthFull();
        setPadding(true);
        setSpacing(true);

        var titulo = new Span("Configurações");
        titulo.getStyle().setFontWeight("bold");
        titulo.getStyle().setFontSize("var(--lumo-font-size-xl)");

        var psi = authenticatedUser.carregarPsicologoLogado();

        var valorField = new NumberField("Valor padrão da consulta (R$)");
        valorField.setPrefixComponent(VaadinIcon.DOLLAR.create());
        valorField.setMin(0);
        valorField.setStep(0.01);
        valorField.setValue(psi.getValorPadraoConsulta() != null ? psi.getValorPadraoConsulta() : 0);
        valorField.setWidth("300px");

        var form = new FormLayout(valorField);

        var salvarButton = new Button("Salvar", VaadinIcon.CHECK.create(), e -> {
            try {
                psi.setValorPadraoConsulta(valorField.getValue());
                psicologoRepository.save(psi);
                Notification.show("Configurações salvas com sucesso.");
            } catch (Exception ex) {
                Notification.show("Erro ao salvar configurações: " + ex.getMessage());
            }
        });
        salvarButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        add(titulo, form, salvarButton);
    }
}
