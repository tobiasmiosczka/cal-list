package com.github.tobiasmiosczka.callist.calendar;

import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.TimeZone;
import net.fortuna.ical4j.model.component.VTimeZone;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class Cal4JUtil {

    private Cal4JUtil() {

    }

    public static DateTime toDateTime(
            final LocalDateTime localDateTime,
            final ZoneId zoneId,
            final VTimeZone vTimeZone) {
        DateTime dateTime = new DateTime();
        dateTime.setTime(localDateTime.atZone(zoneId).toInstant().toEpochMilli());
        dateTime.setTimeZone(new TimeZone(vTimeZone));
        return dateTime;
    }

}
