package com.gaufoo.bbs.components.scutMajor.common;

import java.util.Objects;

final public class MajorValue {
    public final School school;
    public final Major major;

    private MajorValue(School school, Major major) {
        this.school = school;
        this.major = major;
    }

    public static MajorValue of(School school, Major major) {
        return new MajorValue(school, major);
    }

    public MajorValue modSchool(School school) {
        return new MajorValue(school, this.major);
    }

    public MajorValue modMajor(Major major) {
        return new MajorValue(this.school, major);
    }

    @Override
    public String toString() {
        return "MajorValue" + "(" + this.school + ", " + this.major + ')';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MajorValue other = (MajorValue) o;
        return Objects.equals(school, other.school) &&
                Objects.equals(major, other.major);
    }

    @Override
    public int hashCode() {
        return Objects.hash(school, major);
    }
}
