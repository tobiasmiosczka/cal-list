package com.github.tobiasmiosczka.callist.controller;

import com.github.tobiasmiosczka.callist.model.Position;
import com.github.tobiasmiosczka.callist.service.SunshineEventService;
import com.github.tobiasmiosczka.callist.service.WorkHoursEventService;
import net.fortuna.ical4j.model.Calendar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.ZoneId;

import static com.github.tobiasmiosczka.callist.configuration.CalendarEncoder.TEXT_CALENDAR;

@Controller
public class CalendarController {
    private final static int SIZE = 365;

    private final SunshineEventService sunshineEventService;
    private final WorkHoursEventService workHoursEventService;

    @Autowired
    public CalendarController(final SunshineEventService sunshineEventService, final WorkHoursEventService workHoursEventService) {
        this.sunshineEventService = sunshineEventService;
        this.workHoursEventService = workHoursEventService;
    }

    //http://localhost:8080/sun/calendar.ics?latitude=51.56227&longitude=6.7434&altitude=0&token=1acd9867-bdda-4e56-afe1-32a7f7ad4913
    @GetMapping(value = "/sun/calendar.ics", produces = TEXT_CALENDAR)
    public ResponseEntity<Calendar> getSunshineCalendars(
            @RequestParam final double latitude,
            @RequestParam final double longitude,
            @RequestParam(defaultValue = "0") final int altitude,
            @RequestParam(defaultValue = "Sunrise") final String sunriseEventSummary,
            @RequestParam(defaultValue = "Sunset") final String sunsetEventSummary,
            @RequestParam final ZoneId zoneId) {
        final LocalDate now = LocalDate.now();
        final Position position = new Position(latitude, longitude, altitude);
        final Calendar calendar = sunshineEventService.getCalendar(
                position,
                now,
                now.plusDays(SIZE),
                zoneId,
                sunriseEventSummary,
                sunsetEventSummary);
        return new ResponseEntity<>(calendar, HttpStatus.OK);
    }

    @GetMapping(value = "/work-hours/calendar.ics", produces = TEXT_CALENDAR)
    public ResponseEntity<Calendar> getWorkHoursCalendar(@RequestParam final ZoneId zoneId) {
        final Calendar calendar = workHoursEventService.getCalendar(zoneId);
        return new ResponseEntity<>(calendar, HttpStatus.OK);
    }
}