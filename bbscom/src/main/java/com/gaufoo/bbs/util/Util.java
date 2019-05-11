package com.gaufoo.bbs.util;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

final public class Util {
    @SafeVarargs
    public static <T> List<T> buildList(T ... objs) {
        return Stream.of(objs).collect(Collectors.toList());
    }

    public static String formatStrWithLen(String str, int len) {
        String fm = "%" + len + "." + len + "s";
        return String.format(fm, str);
    }
}
