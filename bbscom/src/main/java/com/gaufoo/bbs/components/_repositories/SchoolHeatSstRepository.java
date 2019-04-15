package com.gaufoo.bbs.components._repositories;

import com.gaufoo.bbs.components.schoolHeat.SchoolHeatRepository;
import com.gaufoo.bbs.components.schoolHeat.common.PostId;
import com.gaufoo.bbs.components.schoolHeat.common.PostInfo;
import com.gaufoo.bbs.util.SstUtils;
import com.gaufoo.bbs.util.Tuple;
import com.gaufoo.sst.SST;
import com.google.gson.Gson;

import java.nio.file.Path;
import java.util.Comparator;
import java.util.stream.Stream;

public class SchoolHeatSstRepository implements SchoolHeatRepository {
    private static final Gson gson = new Gson();
    private final String repositoryName;
    private final SST idByHeat;
    private final SST idByTime;
    private final SST idToInfo;

    private SchoolHeatSstRepository(String repositoryName, Path storingPath) {
        this.repositoryName = repositoryName;

        String postIdToIdByTime = repositoryName + "time-id";
        idByTime = SST.of(postIdToIdByTime, storingPath.resolve(postIdToIdByTime));

        String postIdToIdByHeat = repositoryName + "heat-id";
        idByHeat = SST.of(postIdToIdByHeat, storingPath.resolve(postIdToIdByHeat));

        String postIdToInfo = repositoryName + "postId-info";
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
        return SstUtils.setEntry(idToInfo, postId.value, gson.toJson(postInfo)) &&
                SstUtils.setEntry(idByTime, gson.toJson(postInfo.latestActiveTime), postId.value) &&
                SstUtils.setEntry(idByHeat, gson.toJson(postInfo.heat), postId.value);
    }

    @Override
    public void deletePostInfo(PostId postId) {
        PostInfo oldPostInfo = SstUtils.getEntry(idToInfo, postId.value,
                postInfo -> gson.fromJson(postInfo, PostInfo.class));
        if (oldPostInfo == null) return;
        SstUtils.removeEntryWithKey(idToInfo, postId.value);
        SstUtils.removeEntryWithKey(idByTime, gson.toJson(oldPostInfo.latestActiveTime));
        SstUtils.removeEntryWithKey(idByHeat, gson.toJson(oldPostInfo.heat));
    }

    @Override
    public void updatePostInfo(PostId postId, PostInfo postInfo) {
        savePostInfo(postId, postInfo);
    }

    @Override
    public void shutdown() {
        SstUtils.waitAllFuturesPar(
                idByHeat.shutdown(),
                idByTime.shutdown(),
                idToInfo.shutdown()
        );
    }

    @Override
    public String getRepositoryName() {
        return repositoryName;
    }

    public static SchoolHeatSstRepository get(String repositoryName, Path storingPath) {
        return new SchoolHeatSstRepository(repositoryName, storingPath);
    }
}
