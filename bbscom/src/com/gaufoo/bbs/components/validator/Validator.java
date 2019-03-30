package com.gaufoo.bbs.components.validator;

import java.util.function.Function;

public interface Validator<T> {
    Boolean validate(T value);

    String getName();

    static <P> Validator<P> defau1t(String componentName, Function<P, Boolean> pred) {
        return new ValidatorImpl<>(componentName, pred);
    }

    default Validator<T> compose(Validator<T> other) {
        return defau1t(this.getName() + "&" + other.getName(), (T s) -> {
            if (this.validate(s)) {
                return other.validate(s);
            } else return false;
        });
    }

    static Validator<String> email() {
        return defau1t("email", (String s) -> {
            if (s == null) return false;
            String regex = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";

            return s.matches(regex);
        });
    }

    static Validator<String> containsDigit() {
        return defau1t("containsDigit", (String s) -> {
            if (s == null) return false;
            return s.matches(".*\\d.*");
        });
    }

    static Validator<String> containsUpper() {
        return defau1t("containsUpper", (String s) -> {
            if (s == null) return false;
            return s.matches(".*[A-Z].*");
        });
    }

    static Validator<String> containsLower() {
        return defau1t("containsLower", (String s) -> {
            if (s == null) return false;
            return s.matches(".*[a-z].*");
        });
    }

    static Validator<String> nonContainsSpace() {
        return defau1t("nonContainsSpace", (String s) -> {
            if (s == null) return false;
            return s.matches("\\S*");
        });
    }

    static Validator<String> minLength(int minLength) {
        return defau1t("length>=" + minLength, (String s) -> {
            if (s == null) return false;
            return s.length() >= minLength;
        });
    }

    static Validator<String> maxLength(int maxLength) {
        return defau1t("length<=" + maxLength, (String s) -> {
            if (s == null) return false;
            return s.length() <= maxLength;
        });
    }
}
