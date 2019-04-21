package com.gaufoo.bbs.components._depr_lostfound.common;

import java.util.Objects;

final public class FoundId {
    public final String value;

    private FoundId(String value) {
        this.value = value;
    }

    public static FoundId of(String value) {
        return new FoundId(value);
    }

    public FoundId modValue(String value) {
        return new FoundId(value);
    }

    @Override
    public String toString() {
        return "FoundId" + "(" + "'" + this.value + "'" + ')';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FoundId other = (FoundId) o;
        return Objects.equals(value, other.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
