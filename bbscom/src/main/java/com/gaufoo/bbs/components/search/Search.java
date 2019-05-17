package com.gaufoo.bbs.components.search;

import com.gaufoo.bbs.components.search.common.DocumentId;
import com.gaufoo.bbs.components.search.common.SearchResult;

import java.util.List;
import java.util.stream.Stream;

public interface Search {
    boolean addDocument(DocumentId documentId, List<String> content);

    boolean removeDocument(DocumentId documentId);

    Stream<SearchResult> search(List<String> queries);

    Stream<SearchResult> search(List<String> queries, String postType);

    static Search defau1t(SearchRepository repository) {
        return new SearchImpl(repository);
    }
}
