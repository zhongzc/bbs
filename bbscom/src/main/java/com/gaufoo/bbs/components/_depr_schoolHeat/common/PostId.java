package com.gaufoo.bbs.components._depr_schoolHeat.common;

import java.util.Objects;

final public class PostId {
    public final String value;

    private PostId(String value) {
        this.value = value;
    }

    public static PostId of(String value) {
        return new PostId(value);
    }

    public PostId modValue(String value) {
        return new PostId(value);
    }

    @Override
    public String toString() {
        return "PostId" + "(" + "'" + this.value + "'" + ')';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PostId other = (PostId) o;
        return Objects.equals(value, other.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
