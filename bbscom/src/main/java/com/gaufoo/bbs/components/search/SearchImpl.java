package com.gaufoo.bbs.components.search;

import com.gaufoo.bbs.components.search.common.DocumentId;
import com.gaufoo.bbs.components.search.common.SearchResult;

import java.util.*;
import java.util.stream.Stream;

public class SearchImpl implements Search {
    private final SearchRepository repository;

    SearchImpl(SearchRepository searchRepository) {
        this.repository = searchRepository;
    }

    @Override
    public boolean addDocument(DocumentId documentId, List<String> content) {
        return repository.addDocument(documentId, content);
    }

    @Override
    public boolean removeDocument(DocumentId documentId) {
        return repository.removeDocument(documentId);
    }

    @Override
    public Stream<SearchResult> search(List<String> queries) {
        long totalDocCounts = repository.totalDocumentCounts();

        return repository.relatedDocuments(queries).stream().map(documentId -> {
            int docWordNum = repository.getDocumentWordCount(documentId);
            double tf_idf = queries.stream().reduce(0.0, (acc, query) -> {
                int occurTimes = repository.getWordOffset(documentId, query).size();
                long occurDocsNum = repository.documentOccursCount(query);
                return acc + (occurTimes * 1.0 / docWordNum) * Math.log(totalDocCounts / occurDocsNum);
            }, Double::sum);

            Map<String, List<Integer>> wordOffset = new HashMap<>();
            queries.forEach(query -> {
                wordOffset.put(query, repository.getWordOffset(documentId, query));
            });

            return SearchResult.of(tf_idf, documentId, wordOffset);
        }).sorted(Collections.reverseOrder(Comparator.comparingDouble(sr -> sr.tf_idf)));
    }

    @Override
    public Stream<SearchResult> search(List<String> queries, String postType) {
        long totalDocCounts = repository.totalDocumentCounts(postType);

        return repository.relatedDocuments(queries).stream()
                .filter(documentId -> documentId.postType.equals(postType))
                .map(documentId -> {
            int docWordNum = repository.getDocumentWordCount(documentId);
            double tf_idf = queries.stream().reduce(0.0, (acc, query) -> {
                int occurTimes = repository.getWordOffset(documentId, query).size();
                long occurDocsNum = repository.documentOccursCount(query, postType);
                return acc + (occurTimes * 1.0 / docWordNum) * Math.log(totalDocCounts / occurDocsNum);
            }, Double::sum);

            Map<String, List<Integer>> wordOffset = new HashMap<>();
            queries.forEach(query -> {
                wordOffset.put(query, repository.getWordOffset(documentId, query));
            });

            return SearchResult.of(tf_idf, documentId, wordOffset);
        }).sorted(Collections.reverseOrder(Comparator.comparingDouble(sr -> sr.tf_idf)));
    }
}
