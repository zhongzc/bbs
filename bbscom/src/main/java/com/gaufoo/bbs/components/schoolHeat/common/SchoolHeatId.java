package com.gaufoo.bbs.components.schoolHeat.common;

import java.util.Objects;

public class SchoolHeatId {
    public final String value;

    private SchoolHeatId(String value) {
        this.value = value;
    }

    public static SchoolHeatId of(String value) {
        return new SchoolHeatId(value);
    }

    public SchoolHeatId modValue(String value) {
        return new SchoolHeatId(value);
    }

    @Override
    public String toString() {
        return "SchoolHeatId" + "(" + "'" + this.value + "'" + ')';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SchoolHeatId other = (SchoolHeatId) o;
        return Objects.equals(value, other.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
