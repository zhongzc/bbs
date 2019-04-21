package com.gaufoo.bbs.components.learningResource.common;

import java.util.Objects;

public class LearningResourceId {
    public final String value;

    private LearningResourceId(String value) {
        this.value = value;
    }

    public static LearningResourceId of(String value) {
        return new LearningResourceId(value);
    }

    public LearningResourceId modValue(String value) {
        return new LearningResourceId(value);
    }

    @Override
    public String toString() {
        return "LearningResourceId" + "(" + "'" + this.value + "'" + ')';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LearningResourceId other = (LearningResourceId) o;
        return Objects.equals(value, other.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
