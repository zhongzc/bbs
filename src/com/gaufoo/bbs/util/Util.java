package com.gaufoo.bbs.util;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

final public class Util {
    @SafeVarargs
    public static <T> List<T> buildList(T ... objs) {
        return Stream.of(objs).collect(Collectors.toList());
    }
}
