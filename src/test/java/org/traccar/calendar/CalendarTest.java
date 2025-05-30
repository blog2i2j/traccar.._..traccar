package org.traccar.calendar;

import net.fortuna.ical4j.data.ParserException;
import org.junit.jupiter.api.Test;
import org.traccar.model.Calendar;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CalendarTest {

    @Test
    public void testCalendar() throws IOException, ParserException, ParseException {
        String calendarString = """
                BEGIN:VCALENDAR
                PRODID:-//Mozilla.org/NONSGML Mozilla Calendar V1.1//EN
                VERSION:2.0
                BEGIN:VTIMEZONE
                TZID:Asia/Yekaterinburg
                BEGIN:STANDARD
                TZOFFSETFROM:+0500
                TZOFFSETTO:+0500
                TZNAME:YEKT
                DTSTART:19700101T000000
                END:STANDARD
                END:VTIMEZONE
                BEGIN:VEVENT
                CREATED:20161213T045151Z
                LAST-MODIFIED:20161213T045242Z
                DTSTAMP:20161213T045242Z
                UID:9d000df0-6354-479d-a407-218dac62c7c9
                SUMMARY:Every night
                RRULE:FREQ=DAILY
                DTSTART;TZID=Asia/Yekaterinburg:20161130T230000
                DTEND;TZID=Asia/Yekaterinburg:20161201T070000
                TRANSP:OPAQUE
                END:VEVENT
                END:VCALENDAR""";
        Calendar calendar = new Calendar();
        calendar.setData(calendarString.getBytes());
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssX");

        assertFalse(calendar.checkMoment(format.parse("2016-12-13 22:59:59+05")));
        assertTrue(calendar.checkMoment(format.parse("2016-12-13 23:00:01+05")));
        assertTrue(calendar.checkMoment(format.parse("2016-12-13 06:59:59+05")));
        assertFalse(calendar.checkMoment(format.parse("2016-12-13 07:00:01+05")));

        var periods = calendar.findPeriods(format.parse("2016-12-13 06:59:59+05"));
        assertFalse(periods.isEmpty());
    }

    @Test
    public void testCalendarOverlap() throws IOException, ParserException, ParseException {
        String calendarString = """
                BEGIN:VCALENDAR
                VERSION:2.0
                PRODID:-//Traccar//NONSGML Traccar//EN
                BEGIN:VEVENT
                UID:00000000-0000-0000-0000-000000000000
                DTSTART;TZID=America/Los_Angeles:20240420T060000
                DTEND;TZID=America/Los_Angeles:20240421T060000
                RRULE:FREQ=DAILY
                SUMMARY:Event
                END:VEVENT
                END:VCALENDAR""";
        Calendar calendar = new Calendar();
        calendar.setData(calendarString.getBytes());
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssX");

        var periods0 = calendar.findPeriods(format.parse("2014-05-13 07:00:00-07"));
        var periods1 = calendar.findPeriods(format.parse("2024-05-13 05:00:00-07"));
        var periods2 = calendar.findPeriods(format.parse("2024-05-13 07:00:00-07"));
        var periods3 = calendar.findPeriods(format.parse("2024-05-13 08:00:00-07"));

        assertEquals(0, periods0.size());
        assertEquals(1, periods1.size());
        assertEquals(1, periods2.size());
        assertEquals(1, periods3.size());

        assertNotEquals(periods0, periods1);
        assertNotEquals(periods1, periods2);
        assertEquals(periods2, periods3);
    }
}
