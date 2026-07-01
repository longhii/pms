package br.com.longhi.views.consultas;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.FlexLayout.FlexWrap;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import org.vaadin.stefan.fullcalendar.CalendarViewImpl;
import org.vaadin.stefan.fullcalendar.FullCalendar;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class CalendarViewToolbar extends HorizontalLayout {

    private final FullCalendar calendar;
    private final Button buttonDatePicker;

    public CalendarViewToolbar(FullCalendar calendar) {
        this.calendar = calendar;

        setWidthFull();
        setPadding(true);
        setSpacing(true);
        setAlignItems(Alignment.CENTER);

        buttonDatePicker = new Button();
        buttonDatePicker.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        var gotoDate = new DatePicker();
        gotoDate.addValueChangeListener(event1 -> {
            if (event1.getValue() != null) {
                calendar.gotoDate(event1.getValue());
            }
        });
        gotoDate.setVisible(false);
        gotoDate.setWidth("0px");
        gotoDate.setHeight("0px");
        buttonDatePicker.getElement().appendChild(gotoDate.getElement());
        buttonDatePicker.addClickListener(event -> gotoDate.open());

        var previousButton = new Button(VaadinIcon.ANGLE_LEFT.create(), e -> calendar.previous());
        previousButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        var nextButton = new Button(VaadinIcon.ANGLE_RIGHT.create(), e -> calendar.next());
        nextButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);

        var viewMonth = new Button("M\u00eas", e -> calendar.changeView(CalendarViewImpl.DAY_GRID_MONTH));
        viewMonth.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY);
        var viewWeek = new Button("Semana", e -> calendar.changeView(CalendarViewImpl.TIME_GRID_WEEK));
        viewWeek.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY);
        var viewDay = new Button("Dia", e -> calendar.changeView(CalendarViewImpl.TIME_GRID_DAY));
        viewDay.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY);
        var viewListMonth = new Button("Lista", e -> calendar.changeView(CalendarViewImpl.LIST_MONTH));
        viewListMonth.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY);

        var dateNav = new HorizontalLayout(previousButton, buttonDatePicker, nextButton);
        dateNav.setAlignItems(Alignment.CENTER);
        dateNav.setPadding(false);
        dateNav.setSpacing(false);

        var views = new FlexLayout(viewMonth, viewWeek, viewDay, viewListMonth);
        views.setAlignItems(Alignment.CENTER);
        views.setFlexWrap(FlexWrap.WRAP);
        views.setJustifyContentMode(JustifyContentMode.END);
        views.getStyle().set("gap", "0");

        var spacer = new Div();
        spacer.getStyle().set("flex", "1");

        add(dateNav, spacer, views);
        setAlignItems(Alignment.CENTER);

        calendar.addDatesRenderedListener(event -> updateInterval(event.getIntervalStart()));
    }

    public void updateInterval(LocalDate intervalStart) {
        if (buttonDatePicker != null) {
            Locale locale = calendar.getLocale();
            buttonDatePicker.setText(intervalStart.format(
                    DateTimeFormatter.ofPattern("MMMM yyyy").withLocale(locale)));
        }
    }
}
