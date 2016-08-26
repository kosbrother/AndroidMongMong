package com.kosbrother.mongmongwoo.utils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DateFormatUtilTest {

    private String CREATED_AT = "2016-07-26T12:01:01.000+08:00";

    @Test
    public void testGetMonthAndDay() throws Exception {
        String result = DateFormatUtil.parseToMonthAndDay(CREATED_AT);
        assertEquals("7月26日", result);
    }

    @Test
    public void testGetDateAndTime() throws Exception {
        assertEquals("2016/07/26 12:01", DateFormatUtil.parseToDateAndTime(CREATED_AT));

        CREATED_AT = "2016-07-26T23:59:01.000+08:00";
        assertEquals("2016/07/26 23:59", DateFormatUtil.parseToDateAndTime(CREATED_AT));

        CREATED_AT = "2016-07-26T00:01:01.000+08:00";
        assertEquals("2016/07/26 00:01", DateFormatUtil.parseToDateAndTime(CREATED_AT));

        CREATED_AT = "2016-07-26T24:01:01.000+08:00";
        assertEquals("2016/07/27 00:01", DateFormatUtil.parseToDateAndTime(CREATED_AT));

    }

    @Test
    public void testParseToYearMonthDay() throws Exception {
        assertEquals("2016/07/26", DateFormatUtil.parseToYearMonthDay(CREATED_AT));
    }
}