package com.github.tobiasmiosczka.callist.service;

import com.github.tobiasmiosczka.callist.DatePropertiesBuilder;
import net.fortuna.ical4j.model.*;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.property.*;
import net.fortuna.ical4j.util.RandomUidGenerator;
import net.fortuna.ical4j.util.UidGenerator;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.github.tobiasmiosczka.callist.Cal4JUtil.toDateTime;
import static java.time.DayOfWeek.*;
import static java.time.LocalTime.of;

@Service
public class WorkHoursEventService {
    private static final UidGenerator UID_GENERATOR = new RandomUidGenerator();

    private static class Shift {
        private final LocalTime from;
        private final LocalTime to;
        private final boolean dayOff;

        public Shift(LocalTime from, LocalTime to) {
            this.from = from;
            this.to = to;
            this.dayOff = false;
        }

        public Shift() {
            this.from = null;
            this.to = null;
            this.dayOff = true;
        }

        public static Shift shift(final LocalTime from, final LocalTime to) {
            return new Shift(from, to);
        }

        public static Shift dayOff() {
            return new Shift();
        }

        public LocalTime getFrom() {
            return from;
        }

        public LocalTime getTo() {
            return to;
        }

        public boolean isDayOff() {
            return dayOff;
        }
    }

    private static final TimeZoneRegistry REGISTRY = TimeZoneRegistryFactory.getInstance().createRegistry();

    public Calendar getCalendar(final ZoneId zoneId) {
        final Map<DayOfWeek, Shift> shifts = Map.ofEntries(
                Map.entry(   MONDAY, Shift.shift(of(7, 0), of(19, 0))),
                Map.entry(  TUESDAY, Shift.shift(of(7, 0), of(19, 0))),
                Map.entry(WEDNESDAY, Shift.shift(of(7, 0), of(19, 0))),
                Map.entry( THURSDAY, Shift.shift(of(7, 0), of(19, 0))),
                Map.entry(   FRIDAY, Shift.shift(of(7, 0), of(19, 0))),
                Map.entry( SATURDAY, Shift.dayOff()),
                Map.entry(   SUNDAY, Shift.dayOff()));
        final VTimeZone vTimeZone = REGISTRY.getTimeZone(zoneId.toString()).getVTimeZone();
        final Calendar calendar = prepareCalendar(vTimeZone);
        calendar.getComponents().addAll(toEvents(shifts, zoneId));
        return calendar;
    }

    private static List<VEvent> toEvents(final Map<DayOfWeek, Shift> shifts, final ZoneId zoneId) {
        final VTimeZone vTimeZone = REGISTRY.getTimeZone(zoneId.toString()).getVTimeZone();
        return Arrays.stream(values())
                .map(dayOfWeek -> toEvents(dayOfWeek, shifts.get(dayOfWeek), zoneId, vTimeZone))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }

    private static Optional<VEvent> toEvents(
            final DayOfWeek dayOfWeek,
            final Shift shift,
            final ZoneId zoneId,
            final VTimeZone vTimeZone) {
        if (shift.isDayOff()) {
            return Optional.empty();
        }
        return Optional.of(generateWeeklyEvent(dayOfWeek, shift.getFrom(), shift.getTo(), zoneId, vTimeZone));
    }

    public static VEvent generateWeeklyEvent(
            final DayOfWeek dayOfWeek,
            final LocalTime start,
            final LocalTime end,
            final ZoneId zoneId,
            final VTimeZone vTimeZone) {
        try {
            return DatePropertiesBuilder.builder()
                    .with(new Summary("Office Hours"))
                    .with(new DtStart(toDateTime(getLocalDateTimeWithGivenTime(dayOfWeek, start), zoneId, vTimeZone)))
                    .with(new DtEnd(toDateTime(getLocalDateTimeWithGivenTime(dayOfWeek, end), zoneId, vTimeZone)))
                    .with(generateWeeklyRule(dayOfWeek))
                    .with(UID_GENERATOR.generateUid())
                    .build();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static LocalDateTime getLocalDateTimeWithGivenTime(final DayOfWeek dayOfWeek, final LocalTime localTime) {
        LocalDateTime now = LocalDateTime.now();
        return now
                .plusDays(dayOfWeek.getValue() - now.getDayOfWeek().getValue())
                .withHour(localTime.getHour())
                .withMinute(localTime.getMinute())
                .withSecond(localTime.getSecond())
                .withNano(localTime.getNano());
    }

    private static RRule generateWeeklyRule(final DayOfWeek dayOfWeek) throws ParseException {
        final String dayOfWeekString = dayOfWeek.toString().substring(0, 2).toUpperCase();
        return new RRule("FREQ=WEEKLY;BYDAY=" + dayOfWeekString);
    }

    private static Calendar prepareCalendar(final VTimeZone vTimeZone) {
        final Calendar calendar = new Calendar();
        calendar.getProperties().add(new ProdId("-//WorkHours//EN"));
        calendar.getProperties().add(Version.VERSION_2_0);
        calendar.getComponents().add(vTimeZone);
        return calendar;
    }
}
