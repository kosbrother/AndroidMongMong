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

    public static boolean notOnlyChinese(String str) {
        int length = str.length();
        for (int i = 0; i < length; i++) {
            char ch = str.charAt(i);
            Character.UnicodeBlock block = Character.UnicodeBlock.of(ch);
            if (!Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS.equals(block) &&
                    !Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS.equals(block) &&
                    !Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A.equals(block) &&
                    !Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B.equals(block)) {
                return true;
            }
        }
        return false;
    }

}
