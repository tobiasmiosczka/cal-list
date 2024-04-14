package com.github.tobiasmiosczka.callist;

import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.*;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.property.*;
import net.fortuna.ical4j.util.RandomUidGenerator;
import net.fortuna.ical4j.util.UidGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
public class SunEventService {

    private static final UidGenerator UID_GENERATOR = new RandomUidGenerator();
    private static final TimeZoneRegistry REGISTRY = TimeZoneRegistryFactory.getInstance().createRegistry();

    private final SunService sunService;

    @Autowired
    public SunEventService(final SunService sunService) {
        this.sunService = sunService;
    }

    public Calendar getCalendar(
            final double latitude,
            final double longitude,
            final int altitude,
            final LocalDate from,
            final LocalDate to,
            final ZoneId zoneId) {
        final VTimeZone vTimeZone = REGISTRY.getTimeZone(zoneId.toString()).getVTimeZone();
        final Calendar calendar = prepareCalendar(vTimeZone, latitude, longitude, altitude);
        final List<SunService.Sunshine> days = sunService.getSunriseSunset(from, to, zoneId, latitude, longitude, altitude);
        final List<VEvent> events = getEvents(days, zoneId, vTimeZone);
        calendar.getComponents().addAll(events);
        return calendar;
    }

    private static Calendar prepareCalendar(
            final VTimeZone vTimeZone,
            double latitude,
            double longitude,
            int altitude) {
        final Calendar calendar = new Calendar();
        calendar.getProperties().add(new ProdId("-//Sun " + longitude + " " + latitude + " " + altitude + "//EN"));
        calendar.getProperties().add(Version.VERSION_2_0);
        calendar.getComponents().add(vTimeZone);
        return calendar;
    }

    private static List<VEvent> getEvents(
            final List<SunService.Sunshine> days,
            final ZoneId zoneId,
            final VTimeZone vTimeZone) {
        return days.stream()
                .map(e -> getvEvent(e, zoneId, vTimeZone))
                .toList();
    }

    private static VEvent getvEvent(final SunService.Sunshine day, final ZoneId zoneId, final VTimeZone vTimeZone) {
        DateTime startDate = toDateTime(day.getSunrise(), zoneId, vTimeZone);
        DateTime endDate = toDateTime(day.getSunset(), zoneId, vTimeZone);
        VEvent event = new VEvent(startDate, endDate, "Sun");
        event.getProperties().add(UID_GENERATOR.generateUid());
        event.getProperties().add(new Description(""));
        event.getProperties().add(new Location(""));
        return event;
    }

    private static DateTime toDateTime(
            final LocalDateTime localDateTime,
            final ZoneId zoneId,
            final VTimeZone vTimeZone) {
        DateTime dateTime = new DateTime();
        dateTime.setTime(localDateTime.atZone(zoneId).toInstant().toEpochMilli());
        dateTime.setTimeZone(new TimeZone(vTimeZone));
        return dateTime;
    }

    public byte[] convertCalendarToByteArray(final Calendar calendar) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        new CalendarOutputter().output(calendar, outputStream);
        return outputStream.toByteArray();
    }
}
