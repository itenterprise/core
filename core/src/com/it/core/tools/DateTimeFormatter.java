package com.it.core.tools;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.format.Time;

import com.it.core.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Класс для форматирования даты и времени
 */
public class DateTimeFormatter {

    private static final String dateFormat = "dd.MM.yy";
    private static final String timeFormat = "HH:mm";
    private static final String dateTimeFormat = "dd.MM.yy, HH:mm";
    private static final String timeDateFormat = "HH:mm, dd.MM.yy";
    private static final String dateExtFormat = "dd.MM.yyyy";
    private static final String dateTimeExtFormat = "dd.MM.yyyy HH:mm";
    private static final String timeDateExtFormat = "HH:mm, dd.MM.yyyy";

    /**
     * Формат даты/времени
     */
    public enum Format {
        /**
         * dd.MM.yy
         */
        DATE,

        /**
         * HH:mm
         */
        TIME,

        /**
         * dd.MM.yy, HH:mm
         */
        DATE_TIME,

        /**
         * HH:mm, dd.MM.yy
         */
        TIME_DATE,

        /**
         * dd.MM.yyyy
         */
        DATE_EXT,

        /**
         * dd.MM.yyyy HH:mm
         */
        DATE_TIME_EXT,

        /**
         * HH:mm, dd.MM.yyyy
         */
        TIME_DATE_EXT
    }

    /**
     * Конвертировать дату в строку
     * @param date Дата
     * @return Строка даты
     */
    public static String getStringDate(Context context, Date date) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat(dateFormat);
        Calendar todayCalendar = Calendar.getInstance();
        Calendar currCalendar = Calendar.getInstance();
        currCalendar.setTime(date);
        if (todayCalendar.get(Calendar.YEAR) == currCalendar.get(Calendar.YEAR)
                && todayCalendar.get(Calendar.DAY_OF_YEAR) == currCalendar.get(Calendar.DAY_OF_YEAR)) {
            dateTimeFormat = new SimpleDateFormat(timeFormat);
        }
        if (todayCalendar.get(Calendar.YEAR) == currCalendar.get(Calendar.YEAR)
                && todayCalendar.get(Calendar.DAY_OF_YEAR) == currCalendar.get(Calendar.DAY_OF_YEAR) + 1) {
            return context.getString(R.string.yesterday);
        }
        if (todayCalendar.get(Calendar.YEAR) == currCalendar.get(Calendar.YEAR)
                && todayCalendar.get(Calendar.DAY_OF_YEAR) == currCalendar.get(Calendar.DAY_OF_YEAR) - 1) {
            return context.getString(R.string.tomorrow);
        }
        return dateTimeFormat.format(date);
    }

    /**
     * Конвертировать дату в строку согласно формату
     * @param date Дата
     * @param format Формат
     * @return Строка даты
     */
    public static String getStringDate(Date date, Format format) {
        if (date == null) {
            return "";
        }
        return new SimpleDateFormat(getStringFormat(format)).format(date);
    }

	/**
	 * Конвертировать время в строку согласно UTC формату
	 * @param date Дата
	 * @return Строка времени
	 */
	public static String getUTCStringDate(Date date) {
		return String.format("Date(%s)", date.getTime());
	}

	/**
	 * Конвертировать время в строку согласно UTC формату
	 * @param time Время
	 * @return Строка времени
	 */
	public static String getUTCStringDate(Time time) {
		return String.format("Date(%s)", time.toMillis(true));
	}

    /**
     * Конвертировать дату в строку
     * @param dayOfMonth День месяца
     * @param monthOfYear Месяц
     * @param year Год
     * @return Строка даты
     */
    public static String getStringDate(int dayOfMonth, int monthOfYear, int year){
        return String.format("%s.%s.%s", getInTimeFormat(dayOfMonth), getInTimeFormat(monthOfYear), year);
    }

    /**
     * Конвертировать время в строку
     * @param minute Минуты
     * @param hour Часы
     * @return Строка времени
     */
    public static String getStringTime(int minute, int hour){
        return String.format("%s:%s", getInTimeFormat(hour), getInTimeFormat(minute));
    }

	/**
	 * Конвертировать строку определенного формата в дату
	 * @param stringDate Строка даты
	 * @param format Формат
	 * @return Дата
	 */
	@Nullable
	public static Date getDateFromString(String stringDate, Format format) {
		if (stringDate == null || stringDate.isEmpty()) {
			return null;
		}
		SimpleDateFormat stringFormat = new SimpleDateFormat(getStringFormat(format));
		Date date = new Date();
		try {
			date = stringFormat.parse(stringDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

    /**
     * Конвертировать число в временной формат XX
     * @param time Время
     * @return Результат в формате XX
     */
    private static String getInTimeFormat(int time){
        String addition = "";
        if (time < 10) {
            addition = "0";
        }
        return addition + String.valueOf(time);
    }

    /**
     * Получить текстовый формат даты
     * @param format Формат
     * @return Текстовый формат
     */
    private static String getStringFormat(Format format){
        String stringFormat;
        switch (format){
            case DATE:
                stringFormat = dateFormat;
                break;
            case TIME:
                stringFormat = timeFormat;
                break;
            case DATE_TIME:
                stringFormat = dateTimeFormat;
                break;
            case TIME_DATE:
                stringFormat = timeDateFormat;
                break;
            case DATE_EXT:
                stringFormat = dateExtFormat;
                break;
            case DATE_TIME_EXT:
                stringFormat = dateTimeExtFormat;
                break;
            case TIME_DATE_EXT:
                stringFormat = timeDateExtFormat;
                break;
            default:
                stringFormat = dateFormat;
                break;
        }
        return stringFormat;
    }
}