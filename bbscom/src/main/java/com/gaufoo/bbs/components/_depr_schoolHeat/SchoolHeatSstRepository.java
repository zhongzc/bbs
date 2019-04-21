package com.gaufoo.bbs.components._depr_schoolHeat;

import com.gaufoo.bbs.components._depr_schoolHeat.common.PostComparators;
import com.gaufoo.bbs.components._depr_schoolHeat.common.PostId;
import com.gaufoo.bbs.components._depr_schoolHeat.common.PostInfo;
import com.gaufoo.bbs.util.SstUtils;
import com.gaufoo.bbs.util.Tuple;
import com.gaufoo.sst.SST;
import com.google.gson.Gson;

import java.nio.file.Path;
import java.time.Instant;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

public class SchoolHeatSstRepository implements SchoolHeatRepository {
    private static final Gson gson = new Gson();
    private final SST idByHeat;
    private final SST idByTime;
    private final SST idToInfo;

    private SchoolHeatSstRepository(Path storingPath) {
        String postIdToIdByTime = "time-id";
        idByTime = SST.of(postIdToIdByTime, storingPath.resolve(postIdToIdByTime));

        String postIdToIdByHeat = "heat-id";
        idByHeat = SST.of(postIdToIdByHeat, storingPath.resolve(postIdToIdByHeat));

        String postIdToInfo = "postId-info";
        idToInfo = SST.of(postIdToInfo, storingPath.resolve(postIdToInfo));
    }

    @Override
    public Stream<PostId> getAllPosts(Comparator<PostInfo> comparator) {
        // very slow!
        return SstUtils.waitFuture(idToInfo.allKeysAsc()
                .thenApply(stringStream -> stringStream
                        .map(key -> Tuple.of(key, SstUtils.waitFuture(
                                idToInfo.get(key)).orElse(null)))
                        .filter(tup -> tup.right.isPresent())
                        .map(tup -> Tuple.of(tup.left, gson.fromJson(tup.right.get(), PostInfo.class)))
                        .sorted((tup1, tup2) -> comparator.compare(tup1.right, tup2.right))
                        .map(tup -> PostId.of(tup.left)))
        ).orElse(Stream.empty());
    }

    @Override
    public Stream<PostId> getAllPosts() {
        return getAllPosts(PostComparators.comparingTime);
    }

    @Override
    public Stream<PostId> allPostsByTimeAsc() {
        return SstUtils.allValuesAsc(idByTime, PostId::of);
    }

    @Override
    public Stream<PostId> allPostsByTimeDes() {
        return SstUtils.allValuesDes(idByTime, PostId::of);
    }

    @Override
    public Stream<PostId> allPostsByHeatAsc() {
        return SstUtils.allValuesAsc(idByHeat, PostId::of);
    }

    @Override
    public Stream<PostId> allPostsByHeatDes() {
        return SstUtils.allValuesDes(idByHeat, PostId::of);
    }

    @Override
    public PostInfo getPostInfo(PostId postId) {
        return SstUtils.getEntry(idToInfo, postId.value,
                postInfo -> gson.fromJson(postInfo, PostInfo.class));
    }

    @Override
    public boolean savePostInfo(PostId postId, PostInfo postInfo) {
        if (SstUtils.contains(idToInfo, postId.value)) return false;
        return putPostInfo(postId, postInfo);
    }

    @Override
    public boolean updatePostInfo(PostId postId, PostInfo postInfo) {
        if (!SstUtils.contains(idToInfo, postId.value)) return false;
        return putPostInfo(postId, postInfo);
    }

    private boolean putPostInfo(PostId postId, PostInfo postInfo) {
        List<CompletionStage<Boolean>> stages = new LinkedList<>();
        stages.add(SstUtils.setEntryAsync(idToInfo, postId.value, gson.toJson(postInfo)));
        stages.add(SstUtils.setEntryAsync(idByTime, format(postInfo.latestActiveTime), postId.value));
        stages.add(SstUtils.setEntryAsync(idByHeat, format(postInfo.heat), postId.value));

        return SstUtils.waitAllFutureParT(stages, true, (r1, r2) -> r1 && r2);
    }

    @Override
    public void deletePostInfo(PostId postId) {
        PostInfo oldPostInfo = SstUtils.getEntry(idToInfo, postId.value,
                postInfo -> gson.fromJson(postInfo, PostInfo.class));
        if (oldPostInfo == null) return;

        SstUtils.waitAllFuturesPar(
                idToInfo.delete(postId.value),
                idByTime.delete(format(oldPostInfo.latestActiveTime)),
                idByHeat.delete(format(oldPostInfo.heat))
        );
    }


    @Override
    public void shutdown() {
        SstUtils.waitAllFuturesPar(
                idByHeat.shutdown(),
                idByTime.shutdown(),
                idToInfo.shutdown()
        );
    }

    private static String format(Instant instant) {
        return String.format("%014d", instant.toEpochMilli());
    }
    private static String format(Integer integer) {
        return String.format("%014d", integer);
    }

    public static SchoolHeatSstRepository get(Path storingPath) {
        return new SchoolHeatSstRepository(storingPath);
    }

}
