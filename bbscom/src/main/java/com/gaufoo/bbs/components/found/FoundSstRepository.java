package com.gaufoo.bbs.components.found;

import com.gaufoo.bbs.components.found.common.FoundId;
import com.gaufoo.bbs.components.found.common.FoundInfo;
import com.gaufoo.bbs.util.SstUtils;
import com.gaufoo.sst.SST;
import com.google.gson.Gson;

import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Stream;

public class FoundSstRepository implements FoundRepository {
    private static final Gson gson = new Gson();
    private final SST idToInfo;

    private FoundSstRepository(Path storingPath) {
        this.idToInfo = SST.of("id-to-info", storingPath);
    }

    @Override
    public Stream<FoundId> getAllPostsAsc() {
        return SstUtils.waitFuture(idToInfo.allKeysAsc()).map(i -> i.map(FoundId::of)).orElse(Stream.empty());
    }

    @Override
    public Stream<FoundId> getAllPostsDes() {
        return SstUtils.waitFuture(idToInfo.allKeysDes()).map(i -> i.map(FoundId::of)).orElse(Stream.empty());
    }

    @Override
    public FoundInfo getPostInfo(FoundId postId) {
        return SstUtils.getEntry(idToInfo, postId.value, info -> gson.fromJson(info, FoundInfo.class));
    }

    @Override
    public boolean savePost(FoundId postId, FoundInfo postInfo) {
        if (SstUtils.contains(idToInfo, postId.value)) return false;
        return SstUtils.setEntry(idToInfo, postId.value, gson.toJson(postInfo));
    }

    @Override
    public boolean updatePost(FoundId postId, FoundInfo postInfo) {
        return Optional.ofNullable(getPostInfo(postId)).map(i ->
                SstUtils.setEntry(idToInfo, postId.value, gson.toJson(postInfo))).orElse(false);
    }

    @Override
    public void deletePost(FoundId postId) {
        SstUtils.removeEntryWithKey(idToInfo, postId.value);
    }

    @Override
    public void shutdown() {
        SstUtils.waitFuture(idToInfo.shutdown());
    }

    public static FoundRepository get(Path storingPath) {
        return new FoundSstRepository(storingPath);
    }
}
