package com.gaufoo.bbs.components.news.common;

import java.util.Objects;

public class NewsId {
    public final String value;

    private NewsId(String value) {
        this.value = value;
    }

    public static NewsId of(String value) {
        return new NewsId(value);
    }

    public NewsId modValue(String value) {
        return new NewsId(value);
    }

    @Override
    public String toString() {
        return "NewsId" + "(" + "'" + this.value + "'" + ')';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NewsId other = (NewsId) o;
        return Objects.equals(value, other.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
