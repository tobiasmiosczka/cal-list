package com.github.tobiasmiosczka.callist;

import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.component.VEvent;

public class EventBuilder {

    private final VEvent actual;
    private final PropertyList<Property> properties;

    public static EventBuilder builder() {
        return new EventBuilder();
    }

    private EventBuilder() {
        this.actual = new VEvent();
        this.properties = actual.getProperties();
    }

    public EventBuilder with(final Property property) {
        properties.add(property);
        return this;
    }

    public VEvent build() {
        return actual;
    }
}
