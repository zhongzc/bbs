package com.gaufoo.bbs.components.content.common;

import java.util.List;
import java.util.Objects;

public class ContentInfo {
    public final List<ContentElem> elems;

    private ContentInfo(List<ContentElem> elems) {
        this.elems = elems;
    }

    public static ContentInfo of(List<ContentElem> elems) {
        return new ContentInfo(elems);
    }

    public ContentInfo modElems(List<ContentElem> elems) {
        return new ContentInfo(elems);
    }

    @Override
    public String toString() {
        return "ContentInfo" + "(" + this.elems + ')';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContentInfo other = (ContentInfo) o;
        return Objects.equals(elems, other.elems);
    }

    @Override
    public int hashCode() {
        return Objects.hash(elems);
    }
}
