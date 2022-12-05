package com.jaldeeinc.jaldee.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import kotlin.collections.CollectionsKt;

public class DateUtils1 {
    /**
     * @param date - which we want full name from
     * @return full day name, for example Friday, Thursday, Monday, etc...
     */
    public static String getDayName(Date date) {
        return new SimpleDateFormat("EEEE", Locale.getDefault()).format(date);
    }

    /**
     * @param date - which we want 3 letters abbreviation from
     * @return day abbreviation, for example Fri, Thu, Mon, etc...
     */
    public static String getDay3LettersName(Date date) {
        return new SimpleDateFormat("EE", Locale.getDefault()).format(date);
    }

    /**
     * @param date - which we want 1 letter abbreviation from
     * @return day abbreviation, for example F, T, M, S, etc...
     */
    public static String getDay1LetterName(Date date) {
        return new SimpleDateFormat("EEEEE", Locale.getDefault()).format(date);
    }

    /**
     * @param date - which we want month number from
     * @return month number, for example 1, 3, 12, 9, etc...
     */
    public static String getMonthNumber(Date date) {
        return new SimpleDateFormat("MM", Locale.getDefault()).format(date);
    }

    /**
     * @param date - which we want month name from
     * @return month name, for example December, September, January, etc...
     */
    public static String getMonthName(Date date) {
        return new SimpleDateFormat("MMMM", Locale.getDefault()).format(date);
    }

    /**
     * @param date - which we want month three letters abbreviation from
     * @return month abbreviation, for example Jan, Feb, Dec, etc...
     */
    public static String getMonth3LettersName(Date date) {
        return new SimpleDateFormat("MMM", Locale.getDefault()).format(date);
    }

    /**
     * @param date - which we want year from
     * @return year, for example 2010, 2019, 2020, 2034...
     */
    public static String getYear(Date date) {
        return new SimpleDateFormat("yyyy", Locale.getDefault()).format(date);
    }

    /**
     * @param date - which we want day number from
     * @return number of day in month, for example 15, 16, 17, etc...
     */
    public static String getDayNumber(Date date) {
        return new SimpleDateFormat("dd", Locale.getDefault()).format(date);
    }

    /**
     * @param date - which we want week number from
     * @return number of week in year, for example 1, 15, 50, etc...
     */
    public static String getNumberOfWeek(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return String.valueOf(cal.get(Calendar.WEEK_OF_YEAR));
    }

    /**
     * @param count - future days count from now which we want to load
     * @return list of future dates with specified length
     */
    public final List getFutureDates(int count) {
        List futureDateList = (List) (new ArrayList());
        Calendar cal = Calendar.getInstance(Locale.getDefault());

        for (int i = 0; i < count; i++) {
            cal.add(Calendar.DATE, 1);
            futureDateList.add(cal.getTime());
        }
        return futureDateList;
    }

    /**
     * @param count - past days count from now which we want to load
     * @return list of past dates with specified length
     */
    public final List getPastDates(int count) {
        List pastDateList = (List) (new ArrayList());
        Calendar cal = Calendar.getInstance(Locale.getDefault());
        for (int i = 0; i < count; i++) {
            cal.add(Calendar.DATE, -1);
            pastDateList.add(cal.getTime());
        }
        return pastDateList;
    }

    /**
     * Simple way to get dates, just using days count
     *
     * @param pastDays           - count of past days, which we want to get
     * @param futureDays         - count of future days, which we want to get
     * @param includeCurrentDate - if true then list will contain current date else won't
     * @return list of dates
     */
    public final List getDates(int pastDays, int futureDays, boolean includeCurrentDate) {

        List var7;
        List futureList = this.getFutureDates(futureDays);
        Calendar cal = Calendar.getInstance(Locale.getDefault());
        List pastList = CollectionsKt.reversed((Iterable)this.getPastDates(pastDays));
        if (includeCurrentDate){
            var7 = Collections.singletonList(pastList.add(cal.getTime()));
            var7.addAll(futureList);
        }else{
            var7 = Collections.singletonList(pastList.addAll(futureList));
        }
        return var7;
    }
}
