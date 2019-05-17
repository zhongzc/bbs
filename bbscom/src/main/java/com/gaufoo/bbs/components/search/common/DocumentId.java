package com.gaufoo.bbs.components.search.common;

import java.util.Objects;

public class DocumentId implements Comparable<DocumentId> {
    public final String postType;
    public final String postId;

    private DocumentId(String postType, String postId) {
        this.postType = postType;
        this.postId = postId;
    }

    public static DocumentId of(String postType, String postId) {
        return new DocumentId(postType, postId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DocumentId that = (DocumentId) o;
        return Objects.equals(postType, that.postType) &&
                Objects.equals(postId, that.postId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(postType, postId);
    }

    @Override
    public int compareTo(DocumentId documentId) {
        return (this.postType + this.postId).compareTo(documentId.postType + documentId.postId);
    }
}
