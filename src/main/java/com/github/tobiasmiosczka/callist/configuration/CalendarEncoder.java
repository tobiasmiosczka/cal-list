package com.github.tobiasmiosczka.callist.configuration;

import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.Calendar;
import org.springframework.core.ResolvableType;
import org.springframework.core.codec.AbstractSingleValueEncoder;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.util.MimeType;
import reactor.core.publisher.Flux;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

public class CalendarEncoder extends AbstractSingleValueEncoder<Calendar> {

    public static final String TEXT_CALENDAR = "text/calendar";
    public static final MimeType MIME_TYPE_TEXT_CALENDAR = new MimeType("text", "calendar");


    public CalendarEncoder() {
        super(MIME_TYPE_TEXT_CALENDAR);
    }

    @Override
    public boolean canEncode(ResolvableType elementType, MimeType mimeType) {
        if (!super.canEncode(elementType, mimeType)) {
            return false;
        }
        return elementType.toClass() == Calendar.class;
    }

    @Override
    protected Flux<DataBuffer> encode(
            Calendar calendar,
            DataBufferFactory dataBufferFactory,
            ResolvableType valueType,
            MimeType mimeType,
            Map<String, Object> hints) {
        return Flux.just(encodeValue(calendar, dataBufferFactory, valueType, mimeType, hints));
    }

    @Override
    public DataBuffer encodeValue(
            Calendar value,
            DataBufferFactory bufferFactory,
            ResolvableType valueType,
            MimeType mimeType,
            Map<String, Object> hints) {
        try {
            return bufferFactory.wrap(convertCalendarToByteArray(value));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] convertCalendarToByteArray(final Calendar calendar) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        new CalendarOutputter().output(calendar, outputStream);
        return outputStream.toByteArray();
    }
}
