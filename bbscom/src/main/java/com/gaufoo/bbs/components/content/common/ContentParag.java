package com.gaufoo.bbs.components.content.common;

import java.util.Objects;

public class ContentParag implements ContentElem {
    public final String paragraph;

    private ContentParag(String paragraph) {
        this.paragraph = paragraph;
    }

    public static ContentParag of(String paragraph) {
        return new ContentParag(paragraph);
    }

    public ContentParag modParagraph(String paragraph) {
        return new ContentParag(paragraph);
    }

    @Override
    public String toString() {
        return "ContentParag" + "(" + "'" + this.paragraph + "'" + ')';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContentParag other = (ContentParag) o;
        return Objects.equals(paragraph, other.paragraph);
    }

    @Override
    public int hashCode() {
        return Objects.hash(paragraph);
    }
}
