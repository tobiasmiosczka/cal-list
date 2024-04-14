package com.github.tobiasmiosczka.callist;

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
public class SunService {

    public static class Sunshine {
        private final LocalDateTime sunrise;
        private final LocalDateTime sunset;

        public Sunshine(final LocalDateTime sunrise, final LocalDateTime sunset) {
            this.sunrise = sunrise;
            this.sunset = sunset;
        }

        public LocalDateTime getSunrise() {
            return sunrise;
        }

        public LocalDateTime getSunset() {
            return sunset;
        }
    }

    public SunService() {
        TZDATA.init();
    }

    public List<Sunshine> getSunriseSunset(
            final LocalDate from,
            final LocalDate to,
            final ZoneId zoneId,
            final double latitude,
            final double longitude,
            final int altitude) {
        final SolarTime solarTime = SolarTime.ofLocation(latitude, longitude, altitude, "NOAA");
        return from.datesUntil(to)
                .map(e -> getSunriseSunset(e, zoneId, solarTime))
                .toList();
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
