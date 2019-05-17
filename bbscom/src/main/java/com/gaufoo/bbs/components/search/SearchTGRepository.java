package com.gaufoo.bbs.components.search;

import com.gaufoo.bbs.components.search.common.DocumentId;
import com.gaufoo.bbs.util.SstUtils;
import com.gaufoo.db.TenGoKV;
import com.gaufoo.db.common.Index;
import com.gaufoo.db.common.IndexFactor;
import com.gaufoo.sst.SST;
import com.google.gson.Gson;

import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class SearchTGRepository implements SearchRepository {
    private final TenGoKV<SearchKey, SearchInfo> db;
    private final SST docIdToCount;
    private TenGoKV.IndexHandler<SearchKey, SearchInfo> hd;
    private final int maxTermLen;

    private SearchTGRepository(Path storing, int maxTermLen) {
        final Gson gson = new Gson();
        this.maxTermLen = maxTermLen;

        this.db = TenGoKV.TenGoKVBuilder.<SearchKey, SearchInfo>get().withPath(storing)
                .keySerializer(this::formatSearchKey, maxTermLen + 2 /*posttype*/ + 8 /*itemId*/)
                .valueSerializer(gson::toJson)
                .keyShaper(this::shapeSearchKey)
                .valueShaper(str -> gson.fromJson(str, SearchInfo.class))
                .withAggregate(Collections.emptyList())
                .withIndex(Index.<SearchKey, SearchInfo>of()
                        .groupBy(IndexFactor.of((k, v) -> k.term, 50), Collections.singletonList(Index.Aggregate.Count))
                        .groupBy(IndexFactor.of((k, v) -> k.postType, 2), Collections.emptyList())
                        .sortBy(IndexFactor.of((k, v) -> k.postType + k.itemId, 10)).build())
                .takeHandler(hd -> this.hd = hd)
                .build();

        docIdToCount = SST.of("docId-count", storing);
    }

    @Override
    public boolean addDocument(DocumentId documentId, List<String> content) {
        int offset = 0;
        boolean result = true;
        for (String term : content) {
            if (term.length() > maxTermLen) {
                offset++;
                continue;
            }
            result = result && this.addTerm(documentId, term, offset++);
        }
        return result && SstUtils.setEntry(docIdToCount, documentId.postType + documentId.postId, String.valueOf(content.size()));
    }

    @Override
    public boolean removeDocument(DocumentId documentId) {
        return db.getAllKeysAsc().map(searchKey -> {
            if (searchKey.postType.equals(documentId.postType) && searchKey.itemId.equals(documentId.postId)) {
                return db.deleteValue(searchKey);
            }
            return true;
        }).reduce(true, (a, b) -> a && b) && SstUtils.removeEntryByKey(docIdToCount, documentId.postType + documentId.postId) != null;
    }

    @Override
    public List<DocumentId> relatedDocuments(List<String> query) {
        return query.stream().map(term ->
            db.getAllKeysAsc(hd, hd.getGroupChain().group(term).endGroup())
                    .map(searchKey -> DocumentId.of(searchKey.postType, searchKey.itemId))
                    .collect(Collectors.toList())
        ).reduce((list1, list2) -> {
            List<DocumentId> newDocIds = new LinkedList<>();
            Iterator<DocumentId> iter1 = list1.iterator();
            Iterator<DocumentId> iter2 = list2.iterator();

            DocumentId doc1 = iter1.hasNext() ? iter1.next() : null;
            DocumentId doc2 = iter2.hasNext() ? iter2.next() : null;
            while (doc1 != null && doc2 != null) {
                if (doc1.equals(doc2)) {
                    newDocIds.add(doc1);
                } else if (doc1.compareTo(doc2) < 0) {
                    doc1 = iter1.hasNext() ? iter1.next() : null;
                } else {
                    doc2 = iter2.hasNext() ? iter2.next() : null;
                }
            }
            return newDocIds;
        }).orElse(Collections.emptyList());
    }

    @Override
    public int getDocumentWordCount(DocumentId documentId) {
        return Optional.ofNullable(SstUtils.getEntry(docIdToCount, documentId.postType + documentId.postId, Integer::parseInt))
                .orElse(0);
    }

    @Override
    public List<Integer> getWordOffset(DocumentId documentId, String term) {
        return db.getValue(SearchKey.of(term, documentId.postType, documentId.postId)).offsets;
    }

    @Override
    public long totalDocumentCounts() {
        return SstUtils.waitFuture(docIdToCount.allKeysAsc()).map(keys ->
                keys.map(k -> Optional.ofNullable(SstUtils.getEntry(docIdToCount, k, Integer::parseInt)).orElse(0))
                        .reduce(0, Integer::sum)
        ).orElse(0);
    }

    @Override
    public long totalDocumentCounts(String postType) {
        return SstUtils.waitFuture(docIdToCount.allKeysAsc()).map(keys ->
                keys.filter(k -> k.substring(0, 2).equals(postType))
                        .map(k -> Optional.ofNullable(SstUtils.getEntry(docIdToCount, k, Integer::parseInt)).orElse(0))
                        .reduce(0, Integer::sum)
        ).orElse(0);
    }

    @Override
    public long documentOccursCount(String term) {
        return db.getAllKeysAsc(hd, hd.getGroupChain().group(term).endGroup()).count();
    }

    @Override
    public long documentOccursCount(String term, String postType) {
        return db.getAllKeysAsc(hd, hd.getGroupChain().group(term).endGroup()).filter(searchKey -> searchKey.postType.equals(postType)).count();
    }

    private boolean addTerm(DocumentId documentId, String term, int offset) {
        SearchKey key = SearchKey.of(term, documentId.postType, documentId.postId);
        SearchInfo info = db.getValue(key);
        SearchInfo newInfo;
        if (info == null) {
            newInfo = SearchInfo.of(new LinkedList<>(Collections.singletonList(offset)));
        } else {
            info.offsets.add(offset);
            newInfo = info;
        }
        return db.saveValue(key, newInfo);
    }

    private String formatSearchKey(SearchKey key) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(key.term);
        for (int i = 0; i < maxTermLen - key.term.length(); ++i) {
            stringBuilder.append(' ');
        }
        stringBuilder.append(key.postType);
        stringBuilder.append(key.itemId);
        return stringBuilder.toString();
    }

    private SearchKey shapeSearchKey(String searchKey) {
        String term = searchKey.substring(0, 50);
        String groupId = searchKey.substring(50, 52);
        String itemId = searchKey.substring(52, 60);

        int i;
        for (i = term.length() - 1; i >= 0; i--) {
            char obj = term.charAt(i);
            if (obj != ' ') {
                i++;
                break;
            }
        }
        return SearchKey.of(term.substring(0, i), groupId, itemId);
    }

    private static class SearchKey {
        public final String term;
        public final String postType;
        public final String itemId;

        private SearchKey(String term, String postType, String itemId) {
            this.term = term;
            this.postType = postType;
            this.itemId = itemId;
        }

        public static SearchKey of(String term, String groupId, String itemId) {
            return new SearchKey(term, groupId, itemId);
        }
    }

    private static class SearchInfo {
        public final List<Integer> offsets;

        private SearchInfo(List<Integer> offsets) {
            this.offsets = offsets;
        }

        public static SearchInfo of(List<Integer> offsets) {
            return new SearchInfo(offsets);
        }
    }
}
