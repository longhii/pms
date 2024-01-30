package com.longhi.pms.views.psicologos;

import com.longhi.pms.views.psicologos.consultorio.ConsultoriosView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import javax.annotation.security.RolesAllowed;
import java.util.Map;
import java.util.Objects;
@Route(value = "")
@RolesAllowed("ROLE_ADMIN")
public class HomeView extends AppLayout {

    private Tabs tabs;

    @Autowired
    private ApplicationContext applicationContext;

//    @Autowired
//    private ConsultoriosView consultorioView;

    private final Map<String, Class<? extends Component>> abas = Map.of("Consultórios", ConsultoriosView.class);

    public HomeView() {
        var toggle = new DrawerToggle();
        criarTabs();

        var title = new H1("Agenda");

        title.getStyle()
                .set("font-size", "var(--lumo-font-size-l)")
                .set("margin", "0");

        tabs.addSelectedChangeListener(event -> {
            var labelTab = event.getSelectedTab().getElement().getChild(1).getText();

            if (Objects.equals(labelTab, "Consultórios")) {
                title.setText(labelTab);
                var bean = applicationContext.getBean(abas.get(labelTab));
                setContent(bean);
            } else {
                System.out.println("Label não habilitada: " + labelTab );
            }
        });

        addToDrawer(tabs);
        addToNavbar(toggle, title);

        setPrimarySection(Section.DRAWER);
    }

    private void criarTabs() {
        var pacienteTab = new Tab(VaadinIcon.USER.create(), new Label("Pacientes"));
        var agendaTab = new Tab(VaadinIcon.CALENDAR.create(), new Label("Agenda"));
        var consultoriosTab = new Tab(VaadinIcon.INSTITUTION.create(), new Label("Consultórios"));
        var pagamentosTab = new Tab(VaadinIcon.MONEY.create(), new Label("Pagamentos"));
        var relatoriosTab = new Tab(VaadinIcon.LINE_CHART.create(), new Label("Relatórios"));

        this.tabs = new Tabs(pacienteTab, agendaTab, consultoriosTab, pagamentosTab, relatoriosTab);

        this.tabs.setOrientation(Tabs.Orientation.VERTICAL);
        this.tabs.setHeight("240px");
        this.tabs.setWidth("240px");
        this.tabs.setSelectedTab(agendaTab);
    }
}
