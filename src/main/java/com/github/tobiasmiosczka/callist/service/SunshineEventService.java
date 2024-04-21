package com.github.tobiasmiosczka.callist.service;

import com.github.tobiasmiosczka.callist.CalendarBuilder;
import com.github.tobiasmiosczka.callist.EventBuilder;
import com.github.tobiasmiosczka.callist.model.DateRange;
import com.github.tobiasmiosczka.callist.model.Position;
import com.github.tobiasmiosczka.callist.model.Sunshine;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.TimeZoneRegistry;
import net.fortuna.ical4j.model.TimeZoneRegistryFactory;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.util.List;
import java.util.stream.Stream;

import static com.github.tobiasmiosczka.callist.Cal4JUtil.toDateTime;

@Service
public class SunshineEventService {

    private static final TimeZoneRegistry REGISTRY = TimeZoneRegistryFactory.getInstance().createRegistry();

    private final SunshineService sunService;

    @Autowired
    public SunshineEventService(final SunshineService sunService) {
        this.sunService = sunService;
    }

    public Calendar getCalendar(
            final Position position,
            final DateRange dateRange,
            final ZoneId zoneId,
            final String sunriseEventSummary,
            final String sunsetEventSummary) {
        final VTimeZone vTimeZone = REGISTRY.getTimeZone(zoneId.toString()).getVTimeZone();
        final List<Sunshine> days = sunService.getSunriseSunset(dateRange, zoneId, position);
        final List<VEvent> events = getEvents(days, zoneId, vTimeZone, sunriseEventSummary, sunsetEventSummary);
        return CalendarBuilder.builder()
                .withId("-//Sun " + position.latitude() + " " + position.longitude() + " " + position.altitude() + "//EN")
                .with(vTimeZone)
                .with(events)
                .build();
    }

    private static List<VEvent> getEvents(
            final List<Sunshine> days,
            final ZoneId zoneId,
            final VTimeZone vTimeZone,
            final String sunriseEventSummary,
            final String sunsetEventSummary) {
        return days.stream()
                .flatMap(e -> Stream.of(
                        EventBuilder.builder()
                                .withStartAndEnd(toDateTime(e.getSunrise(), zoneId, vTimeZone))
                                .withSummary(sunriseEventSummary)
                                .build(),
                        EventBuilder.builder()
                                .withStartAndEnd(toDateTime(e.getSunset(), zoneId, vTimeZone))
                                .withSummary(sunsetEventSummary)
                                .build()))
                .toList();
    }

}
