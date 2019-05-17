package com.gaufoo.bbs.components.search;

import com.gaufoo.bbs.components.search.common.DocumentId;

import java.util.List;

public interface SearchRepository {
    boolean addDocument(DocumentId documentId, List<String> content);

    boolean removeDocument(DocumentId documentId);

    List<DocumentId> relatedDocuments(List<String> query);
    int getDocumentWordCount(DocumentId documentId);
    List<Integer> getWordOffset(DocumentId documentId, String term);

    long totalDocumentCounts();
    long totalDocumentCounts(String postType);
    long documentOccursCount(String term);
    long documentOccursCount(String term, String postType);
}
