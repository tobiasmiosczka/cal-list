package com.github.tobiasmiosczka.callist.calendar;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Version;

import java.util.ArrayList;
import java.util.List;

public class CalendarBuilder {

    private final List<Property> properties = new ArrayList<>();
    private final List<CalendarComponent> components = new ArrayList<>();
    private final Version version;

    public static CalendarBuilder builder() {
        return builder(new Version());
    }

    public static CalendarBuilder builder(Version version) {
        return new CalendarBuilder(version);
    }

    private CalendarBuilder(Version version) {
        this.version = version;
    }

    public Calendar build() {
        Calendar result = new Calendar();
        result.add(version);
        result.addAll(this.properties);
        for (CalendarComponent component : this.components) {
            result.add(component);
        }
        return result;
    }

    public CalendarBuilder withId(final String value) {
        return withProperty(new ProdId(value));
    }

    public CalendarBuilder withProperty(final Property property) {
        this.properties.add(property);
        return this;
    }

    public CalendarBuilder withComponent(final CalendarComponent component) {
        this.components.add(component);
        return this;
    }

    public CalendarBuilder withEvents(final List<VEvent> events) {
        this.components.addAll(events);
        return this;
    }
}
