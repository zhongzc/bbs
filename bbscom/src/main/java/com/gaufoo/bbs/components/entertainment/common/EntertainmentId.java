package com.gaufoo.bbs.components.entertainment.common;

import java.util.Objects;

public class EntertainmentId {
    final public String value;

    private EntertainmentId(String value) {
        this.value = value;
    }

    public static EntertainmentId of(String value) {
        return new EntertainmentId(value);
    }

    public EntertainmentId modValue(String value) {
        return new EntertainmentId(value);
    }

    @Override
    public String toString() {
        return "EntertainmentId" + "(" + "'" + this.value + "'" + ')';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EntertainmentId other = (EntertainmentId) o;
        return Objects.equals(value, other.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
