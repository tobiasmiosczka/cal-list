package com.github.tobiasmiosczka.callist.model;

import java.time.LocalDateTime;

public class Sunshine {
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