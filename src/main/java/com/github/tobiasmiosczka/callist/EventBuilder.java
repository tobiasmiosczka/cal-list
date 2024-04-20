package com.github.tobiasmiosczka.callist;

import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.VEvent;

import java.util.ArrayList;
import java.util.List;

public class EventBuilder {

    private final List<Property> properties;

    public static EventBuilder builder() {
        return new EventBuilder();
    }

    private EventBuilder() {
        this.properties = new ArrayList<>();
    }

    public EventBuilder with(final Property property) {
        this.properties.add(property);
        return this;
    }

    public VEvent build() {
        final VEvent result = new VEvent();
        result.getProperties().addAll(properties);
        return result;
    }
}
