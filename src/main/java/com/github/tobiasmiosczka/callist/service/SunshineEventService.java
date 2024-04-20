package com.github.tobiasmiosczka.callist.service;

import com.github.tobiasmiosczka.callist.CalendarBuilder;
import com.github.tobiasmiosczka.callist.EventBuilder;
import com.github.tobiasmiosczka.callist.model.Sunshine;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.TimeZoneRegistry;
import net.fortuna.ical4j.model.TimeZoneRegistryFactory;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

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
            final double latitude,
            final double longitude,
            final int altitude,
            final LocalDate from,
            final LocalDate to,
            final ZoneId zoneId,
            final String sunriseEventSummary,
            final String sunsetEventSummary) {
        final VTimeZone vTimeZone = REGISTRY.getTimeZone(zoneId.toString()).getVTimeZone();
        final List<Sunshine> days = sunService.getSunriseSunset(from, to, zoneId, latitude, longitude, altitude);
        final List<VEvent> events = getEvents(days, zoneId, vTimeZone, sunriseEventSummary, sunsetEventSummary);
        return CalendarBuilder.builder()
                .withId("-//Sun " + longitude + " " + latitude + " " + altitude + "//EN")
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
                .flatMap(e -> getvEvents(e, zoneId, vTimeZone, sunriseEventSummary, sunsetEventSummary).stream())
                .toList();
    }

    private static List<VEvent> getvEvents(
            final Sunshine day,
            final ZoneId zoneId,
            final VTimeZone vTimeZone,
            final String sunriseEventSummary,
            final String sunsetEventSummary) {
        final DateTime sunrise = toDateTime(day.getSunrise(), zoneId, vTimeZone);
        final DateTime sunset = toDateTime(day.getSunset(), zoneId, vTimeZone);
        return List.of(
                EventBuilder.builder()
                        .withStart(sunrise)
                        .withEnd(sunrise)
                        .withSummary(sunriseEventSummary)
                        .build(),
                EventBuilder.builder()
                        .withStart(sunset)
                        .withEnd(sunset)
                        .withSummary(sunsetEventSummary)
                        .build());
    }

}
