package br.com.longhi.views.consultas;

import com.vaadin.flow.component.HasText;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import org.vaadin.stefan.fullcalendar.CalendarView;
import org.vaadin.stefan.fullcalendar.CalendarViewImpl;
import org.vaadin.stefan.fullcalendar.FullCalendar;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class CalendarViewToolbar extends MenuBar {

    private final FullCalendar calendar;
    private Button buttonDatePicker;
    private CalendarView selectedView = CalendarViewImpl.DAY_GRID_MONTH;

    public CalendarViewToolbar(FullCalendar calendar) {
        this.calendar = calendar;
        initDateItems();
        this.calendar.addDatesRenderedListener(event -> this.updateInterval(event.getIntervalStart()));

    }

    private void initDateItems() {
        addItem(VaadinIcon.ANGLE_LEFT.create(), e -> calendar.previous()).setId("period-previous-button");

        var gotoDate = new DatePicker();
        gotoDate.addValueChangeListener(event1 -> calendar.gotoDate(event1.getValue()));
        gotoDate.getElement().getStyle().set("visibility", "hidden");
        gotoDate.getElement().getStyle().set("position", "fixed");
        gotoDate.setWidth("0px");
        gotoDate.setHeight("0px");
        gotoDate.setWeekNumbersVisible(true);
        buttonDatePicker = new Button(VaadinIcon.CALENDAR.create());
        buttonDatePicker.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        buttonDatePicker.getElement().appendChild(gotoDate.getElement());
        buttonDatePicker.addClickListener(event -> gotoDate.open());
        buttonDatePicker.setWidthFull();
        addItem(buttonDatePicker);
        addItem(VaadinIcon.ANGLE_RIGHT.create(), e -> calendar.next());
    }

    public void updateInterval(LocalDate intervalStart) {
        if (buttonDatePicker != null && selectedView != null) {
            updateIntervalLabel(buttonDatePicker, selectedView, intervalStart);
        }
    }

    void updateIntervalLabel(HasText intervalLabel, CalendarView view, LocalDate intervalStart) {
        Locale locale = calendar.getLocale();
        intervalLabel.setText(intervalStart.format(DateTimeFormatter.ofPattern("MMMM yyyy").withLocale(locale)));
    }
}
