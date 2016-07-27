package com.kosbrother.mongmongwoo.utils;

public class StringUtil {

    public static String transToFullWidth(String s) {
        char c[] = s.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == ' ') {
                c[i] = '\u3000';
            } else if (c[i] < '\177') {
                c[i] = (char) (c[i] + 65248);
            }
        }
        return new String(c);
    }

}
