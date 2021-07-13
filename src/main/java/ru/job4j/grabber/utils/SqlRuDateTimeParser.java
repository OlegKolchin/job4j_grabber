package ru.job4j.grabber.utils;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;

public class SqlRuDateTimeParser implements DateTimeParser {

    private static final DateFormatSymbols myDate = new DateFormatSymbols() {
        @Override
        public String[] getMonths() {
            return new String[]{"янв", "фев", "мар", "апр", "май", "июн",
                    "июл", "авг", "сен", "окт", "ноя", "дек"};
        }
    };

    @Override
    public LocalDateTime parse(String parse) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yy, hh:mm", myDate);
        String[] sql = parse.split(", ");
        if (sql[0].equals("сегодня")) {
            return LocalDateTime.of(LocalDate.now(), LocalTime.parse(sql[1]));
        } else if(sql[0].equals("вчера")) {
            return LocalDateTime.of(LocalDate.now().minusDays(1), LocalTime.parse(sql[1]));
        }
        return dateFormat.parse(parse).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
}