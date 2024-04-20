package com.github.tobiasmiosczka.callist.service;

import com.github.tobiasmiosczka.callist.EventBuilder;
import com.github.tobiasmiosczka.callist.model.Sunshine;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.TimeZoneRegistry;
import net.fortuna.ical4j.model.TimeZoneRegistryFactory;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.property.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
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
        final Calendar calendar = prepareCalendar(vTimeZone, latitude, longitude, altitude);
        final List<Sunshine> days = sunService.getSunriseSunset(from, to, zoneId, latitude, longitude, altitude);
        final List<VEvent> events = getEvents(days, zoneId, vTimeZone, sunriseEventSummary, sunsetEventSummary);
        calendar.getComponents().addAll(events);
        return calendar;
    }

    private static Calendar prepareCalendar(
            final VTimeZone vTimeZone,
            final double latitude,
            final double longitude,
            final int altitude) {
        final Calendar calendar = new Calendar();
        calendar.getProperties().add(new ProdId("-//Sun " + longitude + " " + latitude + " " + altitude + "//EN"));
        calendar.getProperties().add(Version.VERSION_2_0);
        calendar.getComponents().add(vTimeZone);
        return calendar;
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
        List<VEvent> result = new ArrayList<>(2);
        result.add(toEvent(toDateTime(day.getSunrise(), zoneId, vTimeZone), sunriseEventSummary));
        result.add(toEvent(toDateTime(day.getSunset(), zoneId, vTimeZone), sunsetEventSummary));
        return result;
    }

    private static VEvent toEvent(final DateTime dateTime, final String summary) {
        return EventBuilder.builder()
                .withStart(dateTime)
                .withEnd(dateTime)
                .withSummary(summary)
                .withDescription("")
                .withLocation("")
                .build();
    }
}
