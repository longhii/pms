package br.com.longhi.views.pagamentos;

import br.com.longhi.data.Pagamento;
import br.com.longhi.views.MainLayout;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import jakarta.annotation.security.PermitAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;

@PageTitle("Pagamentos")
@Route(value = "pagamentos", layout = MainLayout.class)
@PermitAll
@Uses(Icon.class)
public class PagamentosView extends Composite<VerticalLayout> {

    private static Logger log = LoggerFactory.getLogger(PagamentosView.class);

    Button addButton;
    Button searchButton;
    HorizontalLayout horizontalSearchBar;
    VerticalLayout verticalButtons;
    HorizontalLayout horizontalButtons;

    public PagamentosView() {
        searchButton = new Button(VaadinIcon.SEARCH.create());
        addButton = new Button(VaadinIcon.PLUS.create());
        horizontalSearchBar = new HorizontalLayout();
        verticalButtons = new VerticalLayout();
        verticalButtons.setSpacing(false);
        verticalButtons.setMargin(false);
        verticalButtons.setPadding(false);
        verticalButtons.setAlignItems(Alignment.END);
        horizontalButtons = new HorizontalLayout();
        horizontalButtons.setSpacing(true);
        horizontalButtons.setMargin(false);
        horizontalButtons.setPadding(false);

        var datePicker2 = new DatePicker();
        var checkbox = new Checkbox();
        var checkbox2 = new Checkbox();

        var minimalistGrid = new Grid(Pagamento.class);
        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");


        horizontalSearchBar.setWidthFull();
        var formLayout = new FormLayout();
        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("600px", 4));
        horizontalSearchBar.add(formLayout);

        formLayout.setWidthFull();
        //getContent().setFlexGrow(1.0, formLayout);
        formLayout.addClassName(Gap.MEDIUM);
        //formLayout.setWidth("100%");
        formLayout.setHeight("min-content");

        var datePicker = new DatePicker();
        datePicker.setPlaceholder("Período inicial");
        datePicker.setWidth("min-content");
        datePicker2.setPlaceholder("Período final");
        datePicker2.setWidth("min-content");
        checkbox.setLabel("Em aberto");
        checkbox.setWidth("120px");

        checkbox2.setLabel("Pago");
        checkbox2.setWidth("min-content");
        minimalistGrid.addThemeVariants(GridVariant.LUMO_COMPACT, GridVariant.LUMO_NO_BORDER,
                GridVariant.LUMO_NO_ROW_BORDERS);

        minimalistGrid.setWidth("100%");
        minimalistGrid.getStyle().set("flex-grow", "0");
        // setGridSampleData(minimalistGrid);
        getContent().add(horizontalSearchBar);

        formLayout.add(datePicker);
        formLayout.add(datePicker2);
        var flexCheck = new FlexLayout();
        flexCheck.add(checkbox);
        flexCheck.add(checkbox2);
        formLayout.add(flexCheck);

        var page = UI.getCurrent().getPage();
        AtomicBoolean phoneEnable = new AtomicBoolean(false);
        AtomicBoolean desktopEnable = new AtomicBoolean(false);

        // utilizado para responsividade
        page.addBrowserWindowResizeListener(l -> {
            boolean isPhone = l.getWidth() < 500;

            if (isPhone && !phoneEnable.get()) {
                desktopEnable.set(false);
                phoneEnable.set(true);

                verticalButtons.add(searchButton, addButton);
                horizontalSearchBar.remove(horizontalButtons);
                horizontalSearchBar.add(verticalButtons);
            } else if (!isPhone && !desktopEnable.get()) {
                phoneEnable.set(false);
                desktopEnable.set(true);

                horizontalButtons.add(searchButton, addButton);
                horizontalSearchBar.remove(verticalButtons);
                horizontalSearchBar.add(horizontalButtons);
            }
        });

        page.retrieveExtendedClientDetails(details -> {
            var width = details.getScreenWidth();
            if (width < 500) {
                verticalButtons.add(searchButton, addButton);
                horizontalSearchBar.remove(horizontalButtons);
                horizontalSearchBar.add(verticalButtons);
            } else {
                horizontalButtons.add(searchButton, addButton);
                horizontalSearchBar.remove(verticalButtons);
                horizontalSearchBar.add(horizontalButtons);
            }
        });

        getContent().add(minimalistGrid);
    }

//    private void setGridSampleData(Grid grid) {
//        grid.setItems(query -> samplePersonService.list(
//                        PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
//                .stream());
//    }
}
