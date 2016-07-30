package com.kosbrother.mongmongwoo.utils;

import org.junit.Assert;
import org.junit.Test;

public class DateFormatUtilTest {

    private static final String CREATED_AT = "2016-07-26T12:01:01.000+08:00";

    @Test
    public void testGetMonthAndDay() throws Exception {
        String result = DateFormatUtil.parseToMonthAndDay(CREATED_AT);
        Assert.assertEquals("7月26日", result);
    }

    @Test
    public void testGetDateAndTime() throws Exception {
        String result = DateFormatUtil.parseToDateAndTime(CREATED_AT);
        Assert.assertEquals("2016/07/26 12:01", result);
    }
}