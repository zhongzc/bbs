package com.gaufoo.bbs.components.content.common;

import java.util.Objects;

public class ContentId {
    public final String value;

    private ContentId(String value) {
        this.value = value;
    }

    public static ContentId of(String value) {
        return new ContentId(value);
    }

    public ContentId modValue(String value) {
        return new ContentId(value);
    }

    @Override
    public String toString() {
        return "ContentId" + "(" + "'" + this.value + "'" + ')';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContentId other = (ContentId) o;
        return Objects.equals(value, other.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
