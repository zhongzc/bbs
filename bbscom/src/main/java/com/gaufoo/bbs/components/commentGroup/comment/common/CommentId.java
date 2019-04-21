package com.gaufoo.bbs.components.commentGroup.comment.common;

import java.util.Objects;

final public class CommentId {
    public final String value;

    private CommentId(String value) {
        this.value = value;
    }

    public static CommentId of(String value) {
        return new CommentId(value);
    }

    public CommentId modValue(String value) {
        return new CommentId(value);
    }

    @Override
    public String toString() {
        return "CommentId" + "(" + "'" + this.value + "'" + ')';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommentId other = (CommentId) o;
        return Objects.equals(value, other.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
