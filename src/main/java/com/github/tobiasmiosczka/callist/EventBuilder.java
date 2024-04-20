package com.github.tobiasmiosczka.callist;

import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.DtEnd;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.Location;
import net.fortuna.ical4j.model.property.RRule;
import net.fortuna.ical4j.model.property.Summary;
import net.fortuna.ical4j.util.RandomUidGenerator;
import net.fortuna.ical4j.util.UidGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.time.DayOfWeek;

public class EventBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventBuilder.class);

    private static final UidGenerator UID_GENERATOR = new RandomUidGenerator();

    private final VEvent actual;
    private final PropertyList<Property> properties;

    private EventBuilder() {
        this.actual = new VEvent();
        this.properties = actual.getProperties();
    }

    public static EventBuilder builder() {
        return new EventBuilder()
                .with(UID_GENERATOR.generateUid());
    }

    public VEvent build() {
        return actual;
    }

    public EventBuilder with(final Property property) {
        properties.add(property);
        return this;
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

    public EventBuilder withRuleWeekly(DayOfWeek dayOfWeek) {
        properties.add(generateWeeklyRule(dayOfWeek));
        return this;
    }

    private static RRule generateWeeklyRule(final DayOfWeek dayOfWeek) {
        final String dayOfWeekString = dayOfWeek.toString().substring(0, 2).toUpperCase();
        try {
            return new RRule("FREQ=WEEKLY;BYDAY=" + dayOfWeekString);
        } catch (ParseException e) {
            LOGGER.error("Could not create ", e);
            throw new RuntimeException(e);
        }
    }
}
