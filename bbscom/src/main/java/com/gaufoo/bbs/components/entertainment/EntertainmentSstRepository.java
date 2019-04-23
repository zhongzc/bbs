package com.gaufoo.bbs.components.entertainment;

import com.gaufoo.bbs.components.entertainment.common.EntertainmentId;
import com.gaufoo.bbs.components.entertainment.common.EntertainmentInfo;
import com.gaufoo.bbs.util.SstUtils;
import com.gaufoo.sst.SST;
import com.google.gson.Gson;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

public class EntertainmentSstRepository implements EntertainmentRepository {
    private static final Gson gson = new Gson();
    private final SST idToInfo;
    private final SST authorIndex;
    private final AtomicLong count;

    private EntertainmentSstRepository(Path storingPath) {
        this.idToInfo = SST.of("id-to-info", storingPath);
        this.authorIndex = SST.of("author-index", storingPath);
        this.count = new AtomicLong(getAllPostsAsc().count());
    }

    @Override
    public Stream<EntertainmentId> getAllPostsAsc() {
        return SstUtils.waitFuture(idToInfo.allKeysAsc()).map(i -> i.map(EntertainmentId::of)).orElse(Stream.empty());
    }

    @Override
    public Stream<EntertainmentId> getAllPostsDes() {
        return SstUtils.waitFuture(idToInfo.allKeysDes()).map(i -> i.map(EntertainmentId::of)).orElse(Stream.empty());
    }

    @Override
    public Stream<EntertainmentId> getAllPostsByAuthorAsc(String authorId) {
        return SstUtils.waitFuture(authorIndex.rangeKeysAsc(authorId + "00000000", authorId + "99999999")
                .thenApply(keys -> keys.map(EntertainmentSstRepository::retrieveId))).orElse(Stream.empty());
    }

    @Override
    public Stream<EntertainmentId> getAllPostsByAuthorDes(String authorId) {
        return SstUtils.waitFuture(authorIndex.rangeKeysDes(authorId + "99999999", authorId + "00000000")
                .thenApply(keys -> keys.map(EntertainmentSstRepository::retrieveId))).orElse(Stream.empty());
    }

    @Override
    public EntertainmentInfo getPostInfo(EntertainmentId postId) {
        return SstUtils.getEntry(idToInfo, postId.value, info -> gson.fromJson(info, EntertainmentInfo.class));
    }

    @Override
    public boolean savePost(EntertainmentId postId, EntertainmentInfo postInfo) {
        if (SstUtils.contains(idToInfo, postId.value)) return false;
        List<CompletionStage<Boolean>> tasks = new ArrayList<>();
        tasks.add(SstUtils.setEntryAsync(idToInfo, postId.value, gson.toJson(postInfo)));
        tasks.add(SstUtils.setEntryAsync(authorIndex, concat(postId, postInfo.authorId), "GAUFOO"));
        if (SstUtils.waitAllFutureParT(tasks, true, (a, b) -> a && b)) {
            this.count.incrementAndGet();
            return true;
        } else return false;
    }

    @Override
    public boolean deletePost(EntertainmentId postId) {
        return Optional.ofNullable(SstUtils.removeEntryByKey(idToInfo, postId.value, info -> gson.fromJson(info, EntertainmentInfo.class))).map(info -> {
            this.count.decrementAndGet();
            return SstUtils.removeEntryByKey(authorIndex, concat(postId, info.authorId)) != null;
        }).orElse(false);
    }

    @Override
    public Long count() {
        return this.count.get();
    }

    @Override
    public void shutdown() {
        SstUtils.waitAllFuturesPar(idToInfo.shutdown(), authorIndex.shutdown());
    }

    private static String concat(EntertainmentId postId, String authorId) {
        return authorId + postId.value;
    }

    private static EntertainmentId retrieveId(String string) {
        return EntertainmentId.of(string.substring(8));
    }

    public static EntertainmentRepository get(Path storingPath) {
        return new EntertainmentSstRepository(storingPath);
    }
}
