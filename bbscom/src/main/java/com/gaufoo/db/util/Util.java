package com.gaufoo.db.util;

public class Util {
    public static String formatStrWithLen(String str, int len) {
        String fm = "%" + len + "." + len + "s";
        return String.format(fm, str);
    }
}
