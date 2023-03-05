package de.phoenixrpg.proofofeducation.utils;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class DateUtils {
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    public static String getDateAsString(LocalDate date) {
            return formatter.format(date);
    }

    public static LocalDate createDateFromString(String dateString) {
        try {
            if(dateString == null) return null;

            String[] dateFormat = dateString.split("\\.");

            int day = Integer.parseInt(dateFormat[0]);
            int month = Integer.parseInt(dateFormat[1]);
            int year = Integer.parseInt(dateFormat[2]);

            LocalDate date = LocalDate.of(year, month, day);
            return checkWeekend(date);
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException | DateTimeException ex) {
            return null;
        }
    }

    public static LocalDate checkWeekend(LocalDate date) {
        if(date.getDayOfWeek() == DayOfWeek.SUNDAY) date = date.minusDays(2);
        if(date.getDayOfWeek() == DayOfWeek.SATURDAY) date = date.plusDays(2);
        return date;
    }
    public static int getDiffYears(Date first, Date last) {
        Calendar a = getCalendar(first);
        Calendar b = getCalendar(last);
        int diff = b.get(Calendar.YEAR) - a.get(Calendar.YEAR);
        if (a.get(Calendar.MONTH) > b.get(Calendar.MONTH) ||
                (a.get(Calendar.MONTH) == b.get(Calendar.MONTH) && a.get(Calendar.DATE) > b.get(Calendar.DATE))) {
            diff--;
        }
        return diff;
    }

    public static Calendar getCalendar(Date date) {
        Calendar cal = Calendar.getInstance(Locale.GERMAN);
        cal.setTime(date);
        return cal;
    }

    public static long getWeeksBetweenDates(Date d1, Date d2){

        Instant d1i = Instant.ofEpochMilli(d1.getTime());
        Instant d2i = Instant.ofEpochMilli(d2.getTime());

        LocalDateTime startDate = LocalDateTime.ofInstant(d1i, ZoneId.systemDefault());
        LocalDateTime endDate = LocalDateTime.ofInstant(d2i, ZoneId.systemDefault());

        return ChronoUnit.WEEKS.between(startDate, endDate)+1;
    }
}
