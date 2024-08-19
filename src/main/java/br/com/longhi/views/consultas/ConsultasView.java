package br.com.longhi.views.consultas;

import br.com.longhi.data.Consulta;
import br.com.longhi.services.ConsultaService;
import br.com.longhi.views.MainLayout;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import elemental.json.Json;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.stefan.fullcalendar.*;
import org.vaadin.stefan.fullcalendar.dataprovider.CallbackEntryProvider;
import org.vaadin.stefan.fullcalendar.dataprovider.EntryProvider;
import org.vaadin.stefan.fullcalendar.dataprovider.EntryQuery;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.util.Locale;

@PageTitle("Consultas")
@Route(value = "", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@PermitAll
@Uses(Icon.class)
public class ConsultasView extends VerticalLayout {

    public ConsultasView(ConsultaService consultaService) {
        setSizeFull();

        var initialOptions = Json.createObject();
        initialOptions.put("initialView", "listMonth");

        CallbackEntryProvider<Entry> call = EntryProvider.fromCallbacks(
                query -> consultaService.streamEntries(query).stream(),
                entryId -> consultaService.getEntry(entryId).orElse(null));

        FullCalendar calendar = FullCalendarBuilder.create()
                .withInitialOptions(initialOptions)
                .withEntryProvider(call)
                .build();

        calendar.setTimezone(new Timezone(ZoneId.of("America/Sao_Paulo")));

        CalendarViewToolbar toolbar = new CalendarViewToolbar(calendar);

        add(toolbar);
        add(calendar);
        setFlexGrow(1, calendar);
        setHorizontalComponentAlignment(Alignment.STRETCH, calendar);
    }
}
