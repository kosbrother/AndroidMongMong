package com.kosbrother.mongmongwoo.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DateFormatUtil {

    public static final String API_CREATED_AT_PATTERN = "yyyy-MM-dd'T'kk:mm:ss.sss";

    public static String parseToMonthAndDay(String createdAt) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(API_CREATED_AT_PATTERN, Locale.getDefault());
        try {
            Calendar cal = Calendar.getInstance();
            cal.setTime(simpleDateFormat.parse(createdAt));
            return (cal.get(Calendar.MONTH) + 1) + "月" + cal.get(Calendar.DAY_OF_MONTH) + "日";
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String parseToDateAndTime(String createdAt) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(API_CREATED_AT_PATTERN, Locale.getDefault());
        try {
            Calendar cal = Calendar.getInstance();
            cal.setTime(simpleDateFormat.parse(createdAt));
            return cal.get(Calendar.YEAR) + "/" +
                    formatLeadingZero(cal.get(Calendar.MONTH) + 1) + "/"
                    + cal.get(Calendar.DATE) + " "
                    + formatLeadingZero(cal.get(Calendar.HOUR_OF_DAY)) + ":" + formatLeadingZero(cal.get(Calendar.MINUTE));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    private static String formatLeadingZero(int integer) {
        return String.format(Locale.getDefault(), "%02d", integer);
    }

}
