package com.github.tobiasmiosczka.callist.controller;

import com.github.tobiasmiosczka.callist.model.DateRange;
import com.github.tobiasmiosczka.callist.model.Position;
import com.github.tobiasmiosczka.callist.service.sunshine.SunshineEventService;
import com.github.tobiasmiosczka.callist.service.officehours.OfficeHoursEventService;
import net.fortuna.ical4j.model.Calendar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.ZoneId;

import static com.github.tobiasmiosczka.callist.configuration.CalendarEncoder.TEXT_CALENDAR;

@Controller
public class CalendarController {

    private static final int SIZE = 365;

    private final SunshineEventService sunshineEventService;
    private final OfficeHoursEventService officeHoursEventService;

    @Autowired
    public CalendarController(final SunshineEventService sunshineEventService, final OfficeHoursEventService officeHoursEventService) {
        this.sunshineEventService = sunshineEventService;
        this.officeHoursEventService = officeHoursEventService;
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
        final DateRange dateRange = new DateRange(now, now.plusDays(SIZE));
        final Calendar calendar = sunshineEventService.getCalendar(
                position,
                dateRange,
                zoneId,
                sunriseEventSummary,
                sunsetEventSummary);
        return new ResponseEntity<>(calendar, HttpStatus.OK);
    }

    @GetMapping(value = "/work-hours/calendar.ics", produces = TEXT_CALENDAR)
    public ResponseEntity<Calendar> getWorkHoursCalendar(@RequestParam final ZoneId zoneId) {
        final Calendar calendar = officeHoursEventService.getCalendar(zoneId);
        return new ResponseEntity<>(calendar, HttpStatus.OK);
    }
}