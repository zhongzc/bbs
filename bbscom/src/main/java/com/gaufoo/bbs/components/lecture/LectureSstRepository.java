package com.gaufoo.bbs.components.lecture;

import com.gaufoo.bbs.components.lecture.common.LectureId;
import com.gaufoo.bbs.components.lecture.common.LectureInfo;
import com.gaufoo.bbs.util.SstUtils;
import com.gaufoo.sst.SST;
import com.google.gson.Gson;

import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

public class LectureSstRepository implements LectureRepository {
    private static final Gson gson = new Gson();
    private final SST idToInfo;
    private final SST timeIndex;

    private LectureSstRepository(Path storingPath) {
        this.idToInfo = SST.of("id-to-info", storingPath);
        this.timeIndex = SST.of("time-index", storingPath);
    }

    @Override
    public Stream<LectureId> getAllPostsAsc() {
        return SstUtils.waitFuture(idToInfo.allKeysAsc()).map(i -> i.map(LectureId::of)).orElse(Stream.empty());
    }

    @Override
    public Stream<LectureId> getAllPostsDes() {
        return SstUtils.waitFuture(idToInfo.allKeysDes()).map(i -> i.map(LectureId::of)).orElse(Stream.empty());
    }

    @Override
    public Stream<LectureId> getAllPostsTimeOrderAsc() {
        return SstUtils.waitFuture(timeIndex.allKeysAsc()).map(i -> i.map(LectureSstRepository::retrieveId)).orElse(Stream.empty());
    }

    @Override
    public Stream<LectureId> getAllPostsTimeOrderDes() {
        return SstUtils.waitFuture(timeIndex.allKeysDes()).map(i -> i.map(LectureSstRepository::retrieveId)).orElse(Stream.empty());
    }

    @Override
    public LectureInfo getPostInfo(LectureId postId) {
        return SstUtils.getEntry(idToInfo, postId.value, info -> gson.fromJson(info, LectureInfo.class));
    }

    @Override
    public boolean savePost(LectureId postId, LectureInfo postInfo) {
        if (SstUtils.contains(idToInfo, postId.value)) return false;
        List<CompletionStage<Boolean>> tasks = new ArrayList<>();
        tasks.add(SstUtils.setEntryAsync(idToInfo, postId.value, gson.toJson(postInfo)));
        tasks.add(SstUtils.setEntryAsync(timeIndex, concat(postId, postInfo.time), "GAUFOO"));
        return SstUtils.waitAllFutureParT(tasks, true, (a, b) -> a && b);
    }

    @Override
    public void deletePost(LectureId postId) {
        Optional.ofNullable(getPostInfo(postId)).ifPresent(info ->
                SstUtils.waitAllFuturesPar(
                        idToInfo.delete(postId.value),
                        timeIndex.delete(concat(postId, info.time))
                )
        );
    }

    @Override
    public void shutdown() {
        SstUtils.waitAllFuturesPar(idToInfo.shutdown(), timeIndex.shutdown());
    }

    private static String concat(LectureId postId, Instant time) {
        return String.format("%014d", time.toEpochMilli()) + postId.value;
    }

    private static LectureId retrieveId(String string) {
        return LectureId.of(string.substring(14));
    }

    public static LectureRepository get(Path storingPath) {
        return new LectureSstRepository(storingPath);
    }
}
