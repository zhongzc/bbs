package com.gaufoo.bbs.components.content.common;

import java.util.Objects;

public class ContentFig implements ContentElem {
    public final String figureId;

    private ContentFig(String figureId) {
        this.figureId = figureId;
    }

    public static ContentFig of(String figureId) {
        return new ContentFig(figureId);
    }

    public ContentFig modFigureId(String figureId) {
        return new ContentFig(figureId);
    }

    @Override
    public String toString() {
        return "ContentFig" + "(" + "'" + this.figureId + "'" + ')';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContentFig other = (ContentFig) o;
        return Objects.equals(figureId, other.figureId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(figureId);
    }
}
