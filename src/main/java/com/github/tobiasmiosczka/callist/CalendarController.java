package com.github.tobiasmiosczka.callist;

import net.fortuna.ical4j.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;

@Controller
public class CalendarController {

    private final SunEventService sunEventService;

    @Autowired
    public CalendarController(final SunEventService sunEventService) {
        this.sunEventService = sunEventService;
    }


    @GetMapping("/sun/calendar.ics") //http://localhost:8080/sun/calendar.ics?latitude=51.56227&longitude=6.7434&altitude=0
    @ResponseBody
    public byte[] getCalendars(
            @RequestParam(required = false) Double latitude,
            @RequestParam(required = false) Double longitude,
            @RequestParam(required = false) Integer altitude) {
        //final double latitude = 51.56227D;
        //final double longitude = 6.7434D;
        //final int altitude = 0;
        if (latitude == null || longitude == null || altitude == null) {
            return new byte[0];
        }
        final LocalDate now = LocalDate.now();
        final int size = 365;
        final ZoneId zoneId = ZoneId.of("Europe/Berlin");
        final Calendar calendar = sunEventService.getCalendar(latitude, longitude, altitude, now, size, zoneId);
        try {
            return sunEventService.convertCalendarToByteArray(calendar);
        } catch (IOException e) {

            return new byte[0];
        }
    }

}