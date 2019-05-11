package com.gaufoo.bbs.components.schoolHeat;

import com.gaufoo.bbs.components.schoolHeat.common.SchoolHeatId;
import com.gaufoo.bbs.components.schoolHeat.common.SchoolHeatInfo;
import com.gaufoo.bbs.util.IndexableSST;
import com.google.gson.Gson;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SchoolHeatIndexSstRepository implements SchoolHeatRepository {
    private static final Gson gson = new Gson();
    private IndexableSST<SchoolHeatId, SchoolHeatInfo> idToInfo;
    private IndexableSST.ExtractorId countingExtractorId;
    private IndexableSST.ExtractorId clusteringExtractorId;

    public SchoolHeatIndexSstRepository(Path storingPath) {
        this.idToInfo = IndexableSST.<SchoolHeatId, SchoolHeatInfo>builder()
                .keySerializer(id -> id.value)
                .keyShaper(SchoolHeatId::of)
                .valueSerializer(gson::toJson)
                .valueShaper(s -> gson.fromJson(s, SchoolHeatInfo.class))
                .withCounting(info -> info.authorId).takeId(id -> countingExtractorId = id)
                .withClustering((id, info) -> info.authorId, 8).takeId(id -> clusteringExtractorId = id)
                .storingPath(storingPath).build();
    }

    @Override
    public Stream<SchoolHeatId> getAllPostsAsc() {
        List<SchoolHeatId> result = idToInfo.getAllValuesAsc().collect(Collectors.toList());
        return result.stream();
    }

    @Override
    public Stream<SchoolHeatId> getAllPostsDes() {
        return idToInfo.getAllValuesDes();
    }

    @Override
    public Stream<SchoolHeatId> getAllPostsByAuthorAsc(String authorId) {
        return idToInfo.getAllValuesAscBy(clusteringExtractorId, authorId);
    }

    @Override
    public Stream<SchoolHeatId> getAllPostsByAuthorDes(String authorId) {
        return idToInfo.getAllValuesDesBy(clusteringExtractorId, authorId);
    }

    @Override
    public SchoolHeatInfo getPostInfo(SchoolHeatId postId) {
        return idToInfo.getValue(postId);
    }

    @Override
    public boolean savePost(SchoolHeatId postId, SchoolHeatInfo postInfo) {
        return idToInfo.saveValue(postId, postInfo);
    }

    @Override
    public boolean deletePost(SchoolHeatId postId) {
        return idToInfo.deleteValue(postId);
    }

    @Override
    public Long count() {
        return idToInfo.count();
    }

    @Override
    public Long countByAuthor(String authorId) {
        return null;
    }

    @Override
    public void shutdown() {
        idToInfo.shutdown();
    }

    public static SchoolHeatRepository get(Path storingPath) {
        return new SchoolHeatIndexSstRepository(storingPath);
    }
}
