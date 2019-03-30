package com.gaufoo.bbs.components.scutMajor.common;

import java.util.Objects;

final public class MajorCode {
    public final String value;

    private MajorCode(String value) {
        this.value = value;
    }

    public static MajorCode of(String value) {
        return new MajorCode(value);
    }

    public MajorCode modValue(String value) {
        return new MajorCode(value);
    }

    @Override
    public String toString() {
        return "MajorCode" + "(" + "'" + this.value + "'" + ')';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MajorCode other = (MajorCode) o;
        return Objects.equals(value, other.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
