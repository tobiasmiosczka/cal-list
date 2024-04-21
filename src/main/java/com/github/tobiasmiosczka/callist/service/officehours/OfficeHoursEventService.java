package com.github.tobiasmiosczka.callist.service.officehours;

import com.github.tobiasmiosczka.callist.calendar.CalendarBuilder;
import com.github.tobiasmiosczka.callist.calendar.EventBuilder;
import com.github.tobiasmiosczka.callist.model.Shift;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.TimeZoneRegistry;
import net.fortuna.ical4j.model.TimeZoneRegistryFactory;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VTimeZone;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.github.tobiasmiosczka.callist.calendar.Cal4JUtil.toDateTime;
import static java.time.DayOfWeek.FRIDAY;
import static java.time.DayOfWeek.MONDAY;
import static java.time.DayOfWeek.THURSDAY;
import static java.time.DayOfWeek.TUESDAY;
import static java.time.DayOfWeek.WEDNESDAY;
import static java.time.DayOfWeek.values;
import static java.time.LocalTime.of;

@Service
public class OfficeHoursEventService {

    private static final TimeZoneRegistry REGISTRY = TimeZoneRegistryFactory.getInstance().createRegistry();

    public Calendar getCalendar(final ZoneId zoneId) {
        final Map<DayOfWeek, Shift> shifts = Map.ofEntries(
                Map.entry(MONDAY, Shift.shift(of(7, 0), of(19, 0))),
                Map.entry(TUESDAY, Shift.shift(of(7, 0), of(19, 0))),
                Map.entry(WEDNESDAY, Shift.shift(of(7, 0), of(19, 0))),
                Map.entry(THURSDAY, Shift.shift(of(7, 0), of(19, 0))),
                Map.entry(FRIDAY, Shift.shift(of(7, 0), of(19, 0))));
        final VTimeZone vTimeZone = REGISTRY.getTimeZone(zoneId.toString()).getVTimeZone();
        return CalendarBuilder.builder()
                .withId("-//WorkHours//EN")
                .with(vTimeZone)
                .withEvents(toEvents(shifts, zoneId))
                .build();
    }

    private List<VEvent> toEvents(final Map<DayOfWeek, Shift> shifts, final ZoneId zoneId) {
        final VTimeZone vTimeZone = REGISTRY.getTimeZone(zoneId.toString()).getVTimeZone();
        return Arrays.stream(values())
                .filter(shifts::containsKey)
                .map(dayOfWeek -> toEvent(dayOfWeek, shifts.get(dayOfWeek), zoneId, vTimeZone))
                .toList();
    }

    private VEvent toEvent(
            final DayOfWeek dayOfWeek,
            final Shift shift,
            final ZoneId zoneId,
            final VTimeZone vTimeZone) {
        final LocalDate date = getNextWeekDay(dayOfWeek);
        return EventBuilder.builder()
                .withSummary("Office Hours")
                .withStart(toDateTime(date.atTime(shift.getStart()), zoneId, vTimeZone))
                .withEnd(toDateTime(date.atTime(shift.getEnd()), zoneId, vTimeZone))
                .withRuleWeekly(dayOfWeek)
                .build();
    }

    private LocalDate getNextWeekDay(final DayOfWeek dayOfWeek) {
        LocalDate now = LocalDate.now();
        long dayOffset = dayOfWeek.getValue() - now.getDayOfWeek().getValue();
        return now.plusDays(dayOffset);
    }

}
