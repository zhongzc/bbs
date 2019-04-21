package com.gaufoo.bbs.components.lost;

import com.gaufoo.bbs.components.lost.common.LostId;
import com.gaufoo.bbs.components.lost.common.LostInfo;
import com.gaufoo.bbs.util.SstUtils;
import com.gaufoo.sst.SST;
import com.google.gson.Gson;

import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Stream;

public class LostSstRepository implements LostRepository {
    private static final Gson gson = new Gson();
    private final SST idToInfo;

    private LostSstRepository(Path storingPath) {
        this.idToInfo = SST.of("id-to-info", storingPath);
    }

    @Override
    public Stream<LostId> getAllPostsAsc() {
        return SstUtils.waitFuture(idToInfo.allKeysAsc()).map(i -> i.map(LostId::of)).orElse(Stream.empty());
    }

    @Override
    public Stream<LostId> getAllPostsDes() {
        return SstUtils.waitFuture(idToInfo.allKeysDes()).map(i -> i.map(LostId::of)).orElse(Stream.empty());
    }

    @Override
    public LostInfo getPostInfo(LostId postId) {
        return SstUtils.getEntry(idToInfo, postId.value, info -> gson.fromJson(info, LostInfo.class));
    }

    @Override
    public boolean savePost(LostId postId, LostInfo postInfo) {
        if (SstUtils.contains(idToInfo, postId.value)) return false;
        return SstUtils.setEntry(idToInfo, postId.value, gson.toJson(postInfo));
    }

    @Override
    public boolean updatePost(LostId postId, LostInfo postInfo) {
        return Optional.ofNullable(getPostInfo(postId)).map(i ->
                SstUtils.setEntry(idToInfo, postId.value, gson.toJson(postInfo))).orElse(false);
    }

    @Override
    public void deletePost(LostId postId) {
        SstUtils.removeEntryWithKey(idToInfo, postId.value);
    }

    @Override
    public void shutdown() {
        SstUtils.waitFuture(idToInfo.shutdown());
    }

    public static LostRepository get(Path storingPath) {
        return new LostSstRepository(storingPath);
    }
}
