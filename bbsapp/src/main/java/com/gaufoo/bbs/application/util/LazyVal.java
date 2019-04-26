package com.gaufoo.bbs.application.util;

import java.util.function.Supplier;

/**
 * NOT thread-safe lazy value.
 * @param <T> type of inner value
 */
public class LazyVal<T> {
    private T cache = null;
    private boolean isInitialized = false;
    private final Supplier<T> calculator;

    public LazyVal(Supplier<T> calculator) {
        this.calculator = calculator;
    }

    public T get() {
        if (!isInitialized) {
            cache = calculator.get();
            isInitialized = true;
        }
        return cache;
    }

    public static <T> LazyVal<T> of(Supplier<T> supplier) {
        return new LazyVal<>(supplier);
    }

    public static <T> LazyVal<T> with(T value) {
        return new LazyVal<>(() -> value);
    }
}
