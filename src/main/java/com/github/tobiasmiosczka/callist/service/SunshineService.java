package com.github.tobiasmiosczka.callist.service;

import com.github.tobiasmiosczka.callist.model.DateRange;
import com.github.tobiasmiosczka.callist.model.Position;
import com.github.tobiasmiosczka.callist.model.Sunshine;
import net.time4j.Moment;
import net.time4j.PlainDate;
import net.time4j.calendar.astro.SolarTime;
import net.time4j.tz.repo.TZDATA;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

@Service
public class SunshineService {

    public static final String CALCULATOR = "NOAA";

    public SunshineService() {
        TZDATA.init();
    }

    public List<Sunshine> getSunriseSunset(
            final DateRange dateRange,
            final ZoneId zoneId,
            final Position position) {
        final SolarTime solarTime = ofPosition(position);
        return dateRange.asDates()
                .map(e -> getSunriseSunset(e, zoneId, solarTime))
                .toList();
    }

    private SolarTime ofPosition(final Position position) {
        return SolarTime.ofLocation(position.latitude(), position.longitude(), position.altitude(), CALCULATOR);
    }

    private Sunshine getSunriseSunset(final LocalDate date, final ZoneId zoneId, final SolarTime solarTime) {
        final Optional<LocalDateTime> sunrise = solarTime.sunrise().apply(PlainDate.from(date))
                .map(Moment::toTemporalAccessor)
                .map(instant -> LocalDateTime.ofInstant(instant, zoneId));
        final Optional<LocalDateTime> sunset = solarTime.sunset().apply(PlainDate.from(date))
                .map(Moment::toTemporalAccessor)
                .map(instant -> LocalDateTime.ofInstant(instant, zoneId));
        if (sunrise.isPresent() && sunset.isPresent()) {
            return new Sunshine(sunrise.get(), sunset.get());
        }
        return null;
    }

}
