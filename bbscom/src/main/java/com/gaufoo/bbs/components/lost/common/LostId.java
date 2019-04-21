package com.gaufoo.bbs.components.lost.common;

import java.util.Objects;

public class LostId {
    public final String value;

    private LostId(String value) {
        this.value = value;
    }

    public static LostId of(String value) {
        return new LostId(value);
    }

    public LostId modValue(String value) {
        return new LostId(value);
    }

    @Override
    public String toString() {
        return "LostId" + "(" + "'" + this.value + "'" + ')';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LostId other = (LostId) o;
        return Objects.equals(value, other.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
