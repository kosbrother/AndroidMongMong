package com.kosbrother.mongmongwoo.utils;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

public class StringUtilTest {

    @Test
    public void testTransToFullWidth() throws Exception {
        String expected = "１";
        String actual = StringUtil.transToFullWidth("1");

        assertEquals(expected, actual);
    }

    @Test
    public void testNotOnlyChinese_Chinese() throws Exception {
        String chinese = "測試";

        boolean result = StringUtil.notOnlyChinese(chinese);

        assertFalse(result);
    }

    @Test
    public void testNotOnlyChinese_English() throws Exception {
        String chinese = "abc";

        boolean result = StringUtil.notOnlyChinese(chinese);

        assertTrue(result);
    }

    @Test
    public void testNotOnlyChinese_containEng() throws Exception {
        String chinese = "測試abc";

        boolean result = StringUtil.notOnlyChinese(chinese);

        assertTrue(result);
    }

    @Test
    public void testNotOnlyChinese_containSpecialSymbols() throws Exception {
        String chinese = "測試!@#";

        boolean result = StringUtil.notOnlyChinese(chinese);

        assertTrue(result);
    }
}