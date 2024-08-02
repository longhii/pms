package br.com.longhi.views;

import br.com.longhi.security.AuthenticatedUser;
import br.com.longhi.views.configuracoes.ConfiguracoesView;
import br.com.longhi.views.consultas.ConsultasView;
import br.com.longhi.views.pacientes.PacientesView;
import br.com.longhi.views.pagamentos.PagamentosView;
import br.com.longhi.views.relatorios.RelatoriosView;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.server.auth.AccessAnnotationChecker;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.vaadin.lineawesome.LineAwesomeIcon;

/**
 * The main view is a top-level placeholder for other views.
 */
public class MainLayout extends AppLayout {

    private H1 viewTitle;

    private AuthenticatedUser authenticatedUser;
    private AccessAnnotationChecker accessChecker;

    public MainLayout(AuthenticatedUser authenticatedUser, AccessAnnotationChecker accessChecker) {
        this.authenticatedUser = authenticatedUser;
        this.accessChecker = accessChecker;

        setPrimarySection(Section.DRAWER);
        addDrawerContent();
        addHeaderContent();
    }

    private void addHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.setAriaLabel("Menu toggle");

        viewTitle = new H1();
        viewTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);

        addToNavbar(true, toggle, viewTitle);
    }

    private void addDrawerContent() {
        Span appName = new Span("pms");
        appName.addClassNames(LumoUtility.FontWeight.SEMIBOLD, LumoUtility.FontSize.LARGE);
        Header header = new Header(appName);

        Scroller scroller = new Scroller(createNavigation());

        addToDrawer(header, scroller, createFooter());
    }

    private SideNav createNavigation() {
        SideNav nav = new SideNav();

        if (accessChecker.hasAccess(ConsultasView.class)) {
            nav.addItem(new SideNavItem("Consultas", ConsultasView.class, LineAwesomeIcon.CALENDAR_ALT.create()));

        }
        if (accessChecker.hasAccess(PacientesView.class)) {
            nav.addItem(new SideNavItem("Pacientes", PacientesView.class, LineAwesomeIcon.USERS_SOLID.create()));

        }
        if (accessChecker.hasAccess(PagamentosView.class)) {
            nav.addItem(new SideNavItem("Pagamentos", PagamentosView.class,
                    LineAwesomeIcon.MONEY_BILL_WAVE_SOLID.create()));

        }
        if (accessChecker.hasAccess(RelatoriosView.class)) {
            nav.addItem(new SideNavItem("Relatórios", RelatoriosView.class, LineAwesomeIcon.CHART_BAR_SOLID.create()));

        }
        if (accessChecker.hasAccess(ConfiguracoesView.class)) {
            nav.addItem(new SideNavItem("Configurações", ConfiguracoesView.class, LineAwesomeIcon.COG_SOLID.create()));

        }

        return nav;
    }

    private Footer createFooter() {
        Footer layout = new Footer();

        var maybeUser = authenticatedUser.get();
        if (maybeUser.isPresent()) {
            var user = maybeUser.get();

            Avatar avatar = new Avatar(user.getNome());
//            StreamResource resource = new StreamResource("profile-pic",
//                    () -> new ByteArrayInputStream(user.getProfilePicture()));
            //avatar.setImageResource(resource);
            avatar.setThemeName("xsmall");
            avatar.getElement().setAttribute("tabindex", "-1");

            MenuBar userMenu = new MenuBar();
            userMenu.setThemeName("tertiary-inline contrast");

            MenuItem userName = userMenu.addItem("");
            Div div = new Div();
            div.add(avatar);
            div.add(user.getNome());
            div.add(new Icon("lumo", "dropdown"));
            div.getElement().getStyle().set("display", "flex");
            div.getElement().getStyle().set("align-items", "center");
            div.getElement().getStyle().set("gap", "var(--lumo-space-s)");
            userName.add(div);
            userName.getSubMenu().addItem("Sign out", e -> {
                authenticatedUser.logout();
            });

            layout.add(userMenu);
        } else {
            Anchor loginLink = new Anchor("login", "Sign in");
            layout.add(loginLink);
        }

        return layout;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        viewTitle.setText(getCurrentPageTitle());
    }

    private String getCurrentPageTitle() {
        PageTitle title = getContent().getClass().getAnnotation(PageTitle.class);
        return title == null ? "" : title.value();
    }
}
