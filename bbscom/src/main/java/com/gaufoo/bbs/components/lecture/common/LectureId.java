package com.gaufoo.bbs.components.lecture.common;

import java.util.Objects;

public class LectureId {
    public final String value;

    private LectureId(String value) {
        this.value = value;
    }

    public static LectureId of(String value) {
        return new LectureId(value);
    }

    public LectureId modValue(String value) {
        return new LectureId(value);
    }

    @Override
    public String toString() {
        return "LectureId" + "(" + "'" + this.value + "'" + ')';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LectureId other = (LectureId) o;
        return Objects.equals(value, other.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
