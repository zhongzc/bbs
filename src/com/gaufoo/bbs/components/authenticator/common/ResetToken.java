package com.gaufoo.bbs.components.authenticator.common;

import java.util.Objects;

public class ResetToken {
    public final String value;

    private ResetToken(String value) {
        this.value = value;
    }

    public static ResetToken of(String value) {
        return new ResetToken(value);
    }

    public ResetToken modValue(String value) {
        return new ResetToken(value);
    }

    @Override
    public String toString() {
        return "ResetToken" + "(" + "'" + this.value + "'" + ')';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ResetToken other = (ResetToken) o;
        return Objects.equals(value, other.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
