package com.github.tobiasmiosczka.callist.service;

import com.github.tobiasmiosczka.callist.EventBuilder;
import com.github.tobiasmiosczka.callist.model.Shift;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.TimeZoneRegistry;
import net.fortuna.ical4j.model.TimeZoneRegistryFactory;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.RRule;
import net.fortuna.ical4j.model.property.Version;
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
import static java.time.DayOfWeek.FRIDAY;
import static java.time.DayOfWeek.MONDAY;
import static java.time.DayOfWeek.SATURDAY;
import static java.time.DayOfWeek.SUNDAY;
import static java.time.DayOfWeek.THURSDAY;
import static java.time.DayOfWeek.TUESDAY;
import static java.time.DayOfWeek.WEDNESDAY;
import static java.time.DayOfWeek.values;
import static java.time.LocalTime.of;

@Service
public class WorkHoursEventService {



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
            return EventBuilder.builder()
                    .withSummary("Office Hours")
                    .withStart(toDateTime(getLocalDateTimeWithGivenTime(dayOfWeek, start), zoneId, vTimeZone))
                    .withEnd(toDateTime(getLocalDateTimeWithGivenTime(dayOfWeek, end), zoneId, vTimeZone))
                    .with(generateWeeklyRule(dayOfWeek))
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
