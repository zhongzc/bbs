package com.gaufoo.bbs.components.comment.reply.common;

import java.util.Objects;

public class ReplyId {
    public final String value;

    private ReplyId(String value) {
        this.value = value;
    }

    public static ReplyId of(String value) {
        return new ReplyId(value);
    }

    public ReplyId modValue(String value) {
        return new ReplyId(value);
    }

    @Override
    public String toString() {
        return "ReplyId" + "(" + "'" + this.value + "'" + ')';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReplyId other = (ReplyId) o;
        return Objects.equals(value, other.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
