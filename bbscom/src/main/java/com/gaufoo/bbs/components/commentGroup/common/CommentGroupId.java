package com.gaufoo.bbs.components.commentGroup.common;

import java.util.Objects;

public class CommentGroupId {
    public final String value;

    private CommentGroupId(String value) {
        this.value = value;
    }

    public static CommentGroupId of(String value) {
        return new CommentGroupId(value);
    }

    public CommentGroupId modValue(String value) {
        return new CommentGroupId(value);
    }

    @Override
    public String toString() {
        return "CommentGroupId" + "(" + "'" + this.value + "'" + ')';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommentGroupId other = (CommentGroupId) o;
        return Objects.equals(value, other.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
