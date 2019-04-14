package com.gaufoo.bbs.components._repositories;

import com.gaufoo.bbs.components.like.LikeComponentRepository;
import com.gaufoo.bbs.components.like.common.LikeId;
import com.gaufoo.bbs.components.like.common.LikeInfo;
import com.gaufoo.bbs.util.SstUtils;
import com.gaufoo.sst.SST;
import com.google.gson.Gson;

import java.nio.file.Path;
import java.util.stream.Stream;

public class LikeComponentSstRepository implements LikeComponentRepository {
    private final Gson gson = new Gson();
    private final String repositoryName;
    private final SST idToInfo;

    private LikeComponentSstRepository(String repositoryName, Path storingDir) {
        this.repositoryName = repositoryName;
        idToInfo = SST.of(repositoryName, storingDir.resolve(repositoryName));
    }


    @Override
    public boolean saveLike(LikeId likeId, LikeInfo likeInfo) {
        return SstUtils.setEntry(idToInfo, likeId.value, gson.toJson(likeInfo));
    }

    @Override
    public boolean updateLike(LikeId likeId, LikeInfo likeInfo) {
        return SstUtils.setEntry(idToInfo, likeId.value, gson.toJson(likeInfo));
    }

    @Override
    public LikeInfo getLikeInfo(LikeId id) {
        return SstUtils.getEntry(idToInfo, id.value, likeInfo ->
                gson.fromJson(likeInfo, LikeInfo.class));
    }

    @Override
    public Stream<LikeId> getAllLike() {
        return SstUtils.waitFuture(idToInfo.allKeysAsc()
                .thenApply(stringStream -> stringStream
                        .map(LikeId::of))
        ).orElse(Stream.empty());
    }

    @Override
    public void removeLike(LikeId id) {
        SstUtils.removeEntryWithKey(idToInfo, id.value);
    }

    @Override
    public void shutdown() {
        SstUtils.waitFuture(idToInfo.shutdown());
    }

    @Override
    public String getRepositoryName() {
        return repositoryName;
    }

    public static LikeComponentSstRepository get(String repositoryName, Path storingPath) {
        return new LikeComponentSstRepository(repositoryName, storingPath);
    }
}
