package com.kosbrother.mongmongwoo.utils;

import junit.framework.Assert;

import org.junit.Test;

public class StringUtilTest {

    @Test
    public void testTransToFullWidth() throws Exception {
        String expected = "１";
        String actual = StringUtil.transToFullWidth("1");

        Assert.assertEquals(expected, actual);
    }
}