package com.github.tobiasmiosczka.callist.model;

import java.time.LocalDate;
import java.util.stream.Stream;

public record DateRange(LocalDate from, LocalDate to) {
    public Stream<LocalDate> asDates() {
        return from.datesUntil(to);
    }
}
