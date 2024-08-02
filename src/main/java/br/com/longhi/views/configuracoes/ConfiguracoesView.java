package br.com.longhi.views.configuracoes;

import br.com.longhi.views.MainLayout;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

@PageTitle("Configurações")
@Route(value = "configuracoes", layout = MainLayout.class)
@PermitAll
@Uses(Icon.class)
public class ConfiguracoesView extends Composite<VerticalLayout> {

    public ConfiguracoesView() {
        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");
    }
}
