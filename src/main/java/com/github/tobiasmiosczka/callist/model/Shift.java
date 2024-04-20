package com.github.tobiasmiosczka.callist.model;

import java.time.LocalTime;

public class Shift {
    private final LocalTime from;
    private final LocalTime to;

    public Shift(LocalTime from, LocalTime to) {
        this.from = from;
        this.to = to;
    }

    public static Shift shift(final LocalTime from, final LocalTime to) {
        return new Shift(from, to);
    }

    public LocalTime getStart() {
        return from;
    }

    public LocalTime getEnd() {
        return to;
    }
}