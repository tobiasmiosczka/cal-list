package com.github.tobiasmiosczka.callist;

import net.fortuna.ical4j.model.Calendar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.UUID;

@Controller
public class CalendarController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CalendarController.class);
    private final static int SIZE = 365;

    private final SunEventService sunEventService;

    @Autowired
    public CalendarController(final SunEventService sunEventService) {
        this.sunEventService = sunEventService;
    }

    //http://localhost:8080/sun/calendar.ics?latitude=51.56227&longitude=6.7434&altitude=0&token=1acd9867-bdda-4e56-afe1-32a7f7ad4913
    @GetMapping("/sun/calendar.ics")
    @ResponseBody
    public byte[] getCalendars(
            ServerHttpRequest serverHttpRequest,
            @RequestParam UUID token,
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam int altitude) {
        LOGGER.info("Token: " + token
                + " Remote: " + serverHttpRequest.getRemoteAddress()
                + " Headers: " + serverHttpRequest.getHeaders().entrySet().stream()
                .map(e -> e.getKey() + ": " + e.getValue())
                .reduce((s1, s2) -> s1 + " " + s2)
                .map(e -> "{" + e + "}")
                .orElse("{}"));
        final LocalDate now = LocalDate.now();
        final ZoneId zoneId = ZoneId.of("Europe/Berlin");
        final Calendar calendar = sunEventService.getCalendar(latitude, longitude, altitude, now, SIZE, zoneId);
        try {
            return sunEventService.convertCalendarToByteArray(calendar);
        } catch (IOException e) {
            return new byte[0];
        }
    }

}