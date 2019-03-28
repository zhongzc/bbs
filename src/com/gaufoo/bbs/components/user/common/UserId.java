package com.gaufoo.bbs.components.user.common;

import java.util.Objects;

final public class UserId {
    public final String value;

    private UserId(String value) {
        this.value = value;
    }

    public static UserId of(String value) {
        return new UserId(value);
    }

    public UserId modValue(String value) {
        return new UserId(value);
    }

    @Override
    public String toString() {
        return "UserId" + "(" + "'" + this.value + "'" + ')';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserId other = (UserId) o;
        return Objects.equals(value, other.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
