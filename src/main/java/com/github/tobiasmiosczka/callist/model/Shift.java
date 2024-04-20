package com.github.tobiasmiosczka.callist.model;

import java.time.LocalTime;

public class Shift {
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