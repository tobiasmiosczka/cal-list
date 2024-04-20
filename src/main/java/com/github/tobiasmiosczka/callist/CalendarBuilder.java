package com.github.tobiasmiosczka.callist;

import net.fortuna.ical4j.model.*;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Version;

import java.util.List;

public class CalendarBuilder {

    private final Calendar actual;
    private final PropertyList<Property> properties;
    private final ComponentList<CalendarComponent> components;

    public static CalendarBuilder builder() {
        return new CalendarBuilder()
                .withVersion(Version.VERSION_2_0);
    }

    private CalendarBuilder withVersion(final Version version) {
        this.properties.add(version);
        return this;
    }

    private CalendarBuilder() {
        this.actual = new Calendar();
        this.properties = this.actual.getProperties();
        this.components = this.actual.getComponents();
    }

    public Calendar build() {
        return this.actual;
    }

    public CalendarBuilder with(final Property property) {
        this.properties.add(property);
        return this;
    }

    public CalendarBuilder with(final CalendarComponent component) {
        this.components.add(component);
        return this;
    }

    public CalendarBuilder withId(final String s) {
        this.properties.add(new ProdId(s));
        return this;
    }

    public CalendarBuilder with(final List<VEvent> events) {
        this.components.addAll(events);
        return this;
    }
}
