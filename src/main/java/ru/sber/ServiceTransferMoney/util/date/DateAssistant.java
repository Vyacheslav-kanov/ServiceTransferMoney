package ru.sber.ServiceTransferMoney.util.date;

import lombok.SneakyThrows;
import ru.sber.ServiceTransferMoney.util.loger.Logger;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateAssistant extends Date{
    private static DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.DATE_FIELD);
    private static String toDayStr = dateFormat.format(new Date());
    private static Date toDay = parseDate(toDayStr);

    public static final String DAY = "DAY";
    public static final String MONTH = "MONTH";
    public static final String YEAR = "YEAR";

    public static boolean isBefore(Date date){
        return toDay.before(date);
    }

    public static boolean isBefore(Date date1, Date date2){
        return date1.before(date2);
    }

    public static boolean isAfter(Date date){
        return toDay.after(date);
    }

    public static boolean isAfter(Date date1, Date date2){
        return date1.after(date2);
    }

    /**
     * Преобразовать строковое представление даты в объект даты.
     * @param dateStr строковое представление даты для преобразования.
     * @return возвращается полученный объект даты.
     */

    @SneakyThrows
    public static Date parseDate(String dateStr){
        String[] split = dateStr.split("\\.");

        String day = (split[0].substring(0, 1).equals("0")) ? split[0].substring(1, 2):split[0];
        String month = (split[1].substring(0, 1).equals("0")) ? split[1].substring(1, 2):split[1];
        String year = (split[2]);

        return dateFormat.parse(day + "." + month + "." + year);
    }

    /**
     * Прибавить к указанной дате количество "DAY", "MONTH" или "YEAR".
     * @param date дата для сложения.
     * @param count количество для увеличения даты.
     * @param meter единицы измерения "DAY", "MONTH" или "YEAR".
     * @return увеличенная дата.
     */

    public static Date plus(Date date, int count, String meter){
        Date result = date;
        if(meter.equals(DAY)) return plusDays(date, count);
        if(meter.equals(MONTH)) return plusMonth(date, count);
        if(meter.equals(YEAR)) return plusYear(date, count);
        return result;
    }

    public static Date plusDays(Date date, int count){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, count);
        return calendar.getTime();
    }

    @SneakyThrows
    public static Date plusMonth(Date date, int count){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, count);
        return calendar.getTime();
    }

    @SneakyThrows
    public static Date plusYear(Date date, int count){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.YEAR, count);
        return calendar.getTime();
    }

    public static Date minusDay(Date date, int count){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, count * -1);
        return calendar.getTime();
    }

    @SneakyThrows
    public static Date minusMonth(Date date, int count){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, count * -1);
        return calendar.getTime();
    }

    @SneakyThrows
    public static Date minusYear(Date date, int count){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.YEAR, count * -1);
        return calendar.getTime();
    }

    public static int getDayOnly(Date date){
        String forSplit = toStringFormat(date);
        return Integer.parseInt(forSplit.split("\\.")[0]);
    }

    public static int getMonthOnly(Date date){
        String forSplit = toStringFormat(date);
        return Integer.parseInt(forSplit.split("\\.")[1]);
    }

    public static int getYearOnly(Date date){
        String forSplit = toStringFormat(date);
        return Integer.parseInt(forSplit.split("\\.")[2]);
    }

    public static String toStringFormat(Date date){
        return dateFormat.format(date);
    }

    public static DateFormat getDateFormat() {
        return dateFormat;
    }

    public static String getToDayStr() {
        return toDayStr;
    }

    public static Date getToDay() {
        return toDay;
    }
}
