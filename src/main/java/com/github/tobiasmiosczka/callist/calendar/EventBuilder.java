package com.github.tobiasmiosczka.callist.calendar;

import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.DtEnd;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.Location;
import net.fortuna.ical4j.model.property.RRule;
import net.fortuna.ical4j.model.property.Summary;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.util.RandomUidGenerator;
import net.fortuna.ical4j.util.UidGenerator;

import java.time.DayOfWeek;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class EventBuilder {

    private static final UidGenerator UID_GENERATOR = new RandomUidGenerator();

    private final Uid uid;
    private final List<Property> properties = new ArrayList<>();

    private EventBuilder(Uid uid) {
        this.uid = uid;
    }

    public static EventBuilder builder() {
        return builder(UID_GENERATOR.generateUid());
    }

    public static EventBuilder builder(Uid uid) {
        return new EventBuilder(uid);
    }

    public VEvent build() {
        VEvent result = new VEvent();
        result.add(uid);
        result.addAll(properties);
        return result;
    }

    public EventBuilder with(final Property property) {
        properties.add(property);
        return this;
    }

    public EventBuilder withStart(final ZonedDateTime start) {
        return with(new DtStart<>(start));
    }

    public EventBuilder withEnd(final ZonedDateTime end) {
        return with(new DtEnd<>(end));
    }

    public EventBuilder withSummary(final String summary) {
        return with(new Summary(summary));
    }

    public EventBuilder withDescription(final String description) {
        return with(new Description(description));
    }

    public EventBuilder withLocation(final String location) {
        return with(new Location(location));
    }

    public EventBuilder withRuleWeekly(final DayOfWeek dayOfWeek) {
        return with(generateWeeklyRule(dayOfWeek));
    }

    private static RRule<ZonedDateTime> generateWeeklyRule(final DayOfWeek dayOfWeek) {
        final String dayOfWeekString = dayOfWeek.toString().substring(0, 2).toUpperCase();
        return new RRule<>("FREQ=WEEKLY;BYDAY=" + dayOfWeekString);
    }

    public EventBuilder withStartAndEnd(final ZonedDateTime dateTime) {
        return withStart(dateTime)
                .withEnd(dateTime);
    }
}
