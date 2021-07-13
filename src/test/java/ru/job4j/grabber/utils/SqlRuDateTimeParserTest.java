package ru.job4j.grabber.utils;

import org.junit.Test;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class SqlRuDateTimeParserTest {

    @Test
    public void whenYesterday() throws ParseException {
        String sql = "вчера, 10:53";
        SqlRuDateTimeParser ps = new SqlRuDateTimeParser();
        LocalDate ld = LocalDate.now().minusDays(1);
        LocalTime tm = LocalTime.parse(sql.split(", ")[1]);
        LocalDateTime exc = LocalDateTime.of(ld, tm);
        assertThat(ps.parse(sql), is(exc));
    }

    @Test
    public void whenToday() throws ParseException {
        String sql = "сегодня, 10:53";
        SqlRuDateTimeParser ps = new SqlRuDateTimeParser();
        LocalDate ld = LocalDate.now();
        LocalTime tm = LocalTime.parse(sql.split(", ")[1]);
        LocalDateTime exc = LocalDateTime.of(ld, tm);
        assertThat(ps.parse(sql), is(exc));
    }

    @Test
    public void whenRegularDate() throws ParseException {
        String sql = "11 июл 21, 10:53";
        SqlRuDateTimeParser ps = new SqlRuDateTimeParser();
        LocalDateTime exc = LocalDateTime.of(2021, 07, 11, 10, 53);
        assertThat(ps.parse(sql), is(exc));
    }
}
