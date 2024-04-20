package com.github.tobiasmiosczka.callist;

import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.*;
import net.fortuna.ical4j.util.RandomUidGenerator;
import net.fortuna.ical4j.util.UidGenerator;

public class EventBuilder {

    private static final UidGenerator UID_GENERATOR = new RandomUidGenerator();

    private final VEvent actual;
    private final PropertyList<Property> properties;

    public static EventBuilder builder() {
        return new EventBuilder()
                .with(UID_GENERATOR.generateUid());
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

    public EventBuilder withStart(DateTime start) {
        properties.add(new DtStart(start));
        return this;
    }

    public EventBuilder withEnd(DateTime end) {
        properties.add(new DtEnd(end));
        return this;
    }

    public EventBuilder withSummary(String summary) {
        properties.add(new Summary(summary));
        return this;
    }

    public EventBuilder withDescription(String description) {
        properties.add(new Description(description));
        return this;
    }

    public EventBuilder withLocation(String location) {
        properties.add(new Location(location));
        return this;
    }
}
