package com.kosbrother.mongmongwoo.utils;

import org.junit.Assert;
import org.junit.Test;

public class DateFormatUtilTest {

    private String CREATED_AT = "2016-07-26T12:01:01.000+08:00";

    @Test
    public void testGetMonthAndDay() throws Exception {
        String result = DateFormatUtil.parseToMonthAndDay(CREATED_AT);
        Assert.assertEquals("7月26日", result);
    }

    @Test
    public void testGetDateAndTime() throws Exception {
        Assert.assertEquals("2016/07/26 12:01", DateFormatUtil.parseToDateAndTime(CREATED_AT));

        CREATED_AT = "2016-07-26T23:59:01.000+08:00";
        Assert.assertEquals("2016/07/26 23:59", DateFormatUtil.parseToDateAndTime(CREATED_AT));

        CREATED_AT = "2016-07-26T00:01:01.000+08:00";
        Assert.assertEquals("2016/07/26 00:01", DateFormatUtil.parseToDateAndTime(CREATED_AT));

        CREATED_AT = "2016-07-26T24:01:01.000+08:00";
        Assert.assertEquals("2016/07/27 00:01", DateFormatUtil.parseToDateAndTime(CREATED_AT));

    }
}