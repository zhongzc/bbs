package com.gaufoo.bbs.components.scutCourse.common;

import java.util.Objects;

public class CourseCode {
    public final String value;

    private CourseCode(String value) {
        this.value = value;
    }

    public static CourseCode of(String value) {
        return new CourseCode(value);
    }

    public CourseCode modValue(String value) {
        return new CourseCode(value);
    }

    @Override
    public String toString() {
        return "CourseCode" + "(" + "'" + this.value + "'" + ')';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CourseCode other = (CourseCode) o;
        return Objects.equals(value, other.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
