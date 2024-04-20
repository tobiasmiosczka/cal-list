package com.github.tobiasmiosczka.callist;

import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.DateProperty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DatePropertiesBuilder {

    private final List<Property> properties;

    public static DatePropertiesBuilder builder() {
        return new DatePropertiesBuilder();
    }

    private DatePropertiesBuilder() {
        this.properties = new ArrayList<>();
    }

    public DatePropertiesBuilder with(final Property property) {
        this.properties.add(property);
        return this;
    }

    public VEvent build() {
        final VEvent result = new VEvent();
        result.getProperties().addAll(properties);
        return result;
    }
}
