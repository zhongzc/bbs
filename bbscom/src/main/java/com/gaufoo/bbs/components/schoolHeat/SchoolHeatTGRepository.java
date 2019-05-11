package com.gaufoo.bbs.components.schoolHeat;

import com.gaufoo.bbs.components.schoolHeat.common.SchoolHeatId;
import com.gaufoo.bbs.components.schoolHeat.common.SchoolHeatInfo;
import com.gaufoo.db.TenGoKV;
import com.gaufoo.db.common.Index;
import com.gaufoo.db.common.IndexFactor;
import com.google.gson.Gson;

import java.nio.file.Path;
import java.util.Collections;
import java.util.stream.Stream;

public class SchoolHeatTGRepository implements SchoolHeatRepository {
    private final TenGoKV<SchoolHeatId, SchoolHeatInfo> db;
    private TenGoKV.IndexHandler<SchoolHeatId, SchoolHeatInfo> authorhdr;

    private SchoolHeatTGRepository(Path storingPath) {
        Gson gson = new Gson();
        this.db = TenGoKV.TenGoKVBuilder.<SchoolHeatId, SchoolHeatInfo>get()
            .withPath(storingPath)
            .keySerializer(i -> i.value, 8)
            .valueSerializer(gson::toJson)
            .keyShaper(SchoolHeatId::of)
            .valueShaper(v -> gson.fromJson(v, SchoolHeatInfo.class))
            .withAggregate(Collections.singletonList(Index.Aggregate.Count))
            .withIndex(
                    Index.<SchoolHeatId, SchoolHeatInfo>of()
                    .groupBy(IndexFactor.of((k, v) -> v.authorId, 8), Collections.singletonList(Index.Aggregate.Count)).build())
                    .takeHandler(hd -> this.authorhdr = hd)
            .build();
    }

    @Override
    public Stream<SchoolHeatId> getAllPostsAsc() {
        return this.db.getAllKeysAsc();
    }

    @Override
    public Stream<SchoolHeatId> getAllPostsDes() {
        return this.db.getAllKeysDes();
    }

    @Override
    public Stream<SchoolHeatId> getAllPostsByAuthorAsc(String authorId) {
        return this.db.getAllKeysAsc(authorhdr, authorhdr.getGroupChain().group(authorId).endGroup());
    }

    @Override
    public Stream<SchoolHeatId> getAllPostsByAuthorDes(String authorId) {
        return this.db.getAllKeysDes(authorhdr, authorhdr.getGroupChain().group(authorId).endGroup());
    }

    @Override
    public SchoolHeatInfo getPostInfo(SchoolHeatId postId) {
        return this.db.getValue(postId);
    }

    @Override
    public boolean savePost(SchoolHeatId postId, SchoolHeatInfo postInfo) {
        return this.db.saveValue(postId, postInfo);
    }

    @Override
    public boolean deletePost(SchoolHeatId postId) {
        return this.db.deleteValue(postId);
    }

    @Override
    public Long count() {
        return this.db.getCount();
    }

    @Override
    public Long countByAuthor(String authorId) {
        return this.db.getCount(authorhdr, authorhdr.getGroupChain().group(authorId).endGroup());
    }

    @Override
    public void shutdown() {
        this.db.shutdown();
    }

    public static SchoolHeatRepository get(Path storingPath) {
        return new SchoolHeatTGRepository(storingPath);
    }
}
