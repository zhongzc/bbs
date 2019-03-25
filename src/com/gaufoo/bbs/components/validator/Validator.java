package com.gaufoo.bbs.components.validator;

import java.util.function.Function;
import java.util.regex.Pattern;

public interface Validator<T> {
    Boolean validate(T value);

    static <P> Validator<P> defau1t(Function<P, Boolean> pred) {
        return new ValidatorImpl<>(pred);
    }

    default Validator<T> compose(Validator<T> other) {
        return defau1t((T s) -> {
            if (this.validate(s)) {
                return other.validate(s);
            } else return false;
        });
    }

    static Validator<String> email() {
        return defau1t((String s) -> {
            if (s == null) return false;
            String regex = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";

            return s.matches(regex);
        });
    }

    static Validator<String> containsDigit() {
        return defau1t((String s) -> {
            if (s == null) return false;
            return s.matches(".*\\d.*");
        });
    }

    static Validator<String> containsUpper() {
        return defau1t((String s) -> {
            if (s == null) return false;
            return s.matches(".*[A-Z].*");
        });
    }

    static Validator<String> containsLower() {
        return defau1t((String s) -> {
            if (s == null) return false;
            return s.matches(".*[a-z].*");
        });
    }

    static Validator<String> nonContainsSpace() {
        return defau1t((String s) -> {
            if (s == null) return false;
            return s.matches("\\S*");
        });
    }

    static Validator<String> minLength(int minLength) {
        return defau1t((String s) -> {
            if (s == null) return false;
            return s.length() >= minLength;
        });
    }

    static Validator<String> maxLength(int maxLength) {
        return defau1t((String s) -> {
            if (s == null) return false;
            return s.length() <= maxLength;
        });
    }
}
