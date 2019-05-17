package com.gaufoo.bbs.components.search.common;

import java.util.List;
import java.util.Map;

public class SearchResult {
    public final double tf_idf;
    public final DocumentId documentId;
    public final Map<String, List<Integer>> wordOffsets;

    private SearchResult(double tf_idf, DocumentId documentId, Map<String, List<Integer>> offsets) {
        this.tf_idf = tf_idf;
        this.documentId = documentId;
        this.wordOffsets = offsets;
    }

    public static SearchResult of(double tf_idf, DocumentId documentId, Map<String, List<Integer>> offsets) {
        return new SearchResult(tf_idf, documentId, offsets);
    }
}
