package br.com.longhi.views.pacientes;

import br.com.longhi.data.Paciente;
import br.com.longhi.views.MainLayout;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import jakarta.annotation.security.PermitAll;

@PageTitle("Pacientes")
@Route(value = "pacientes", layout = MainLayout.class)
@PermitAll
@Uses(Icon.class)
public class PacientesView extends Composite<VerticalLayout> {

    Button adicionarPacienteButton;
    Grid pacientesGrid;

    public PacientesView() {
        var layoutRow = new HorizontalLayout();
        adicionarPacienteButton = new Button(VaadinIcon.PLUS.create());
        pacientesGrid = new Grid(Paciente.class);
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

    private void criarDialogAdicionarPaciente() {

    }

//    private void setGridSampleData(Grid grid) {
//        grid.setItems(query -> samplePersonService.list(
//                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
//                .stream());
//    }
}
