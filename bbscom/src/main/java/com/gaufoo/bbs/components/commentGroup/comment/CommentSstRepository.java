package com.gaufoo.bbs.components.commentGroup.comment;

import com.gaufoo.bbs.components.commentGroup.comment.common.CommentId;
import com.gaufoo.bbs.components.commentGroup.comment.common.CommentInfo;
import com.gaufoo.bbs.util.SstUtils;
import com.gaufoo.sst.SST;
import com.google.gson.Gson;

import java.nio.file.Path;

public class CommentSstRepository implements CommentRepository {
    private static final Gson gson = new Gson();
    private final SST idToInfo;

    private CommentSstRepository(Path storingPath) {
        this.idToInfo = SST.of("id-to-info", storingPath);
    }

    @Override
    public boolean saveComment(CommentId id, CommentInfo commentInfo) {
        if (SstUtils.contains(idToInfo, id.value)) return false;
        return SstUtils.setEntry(idToInfo, id.value, gson.toJson(commentInfo));
    }

    @Override
    public CommentInfo getComment(CommentId id) {
        return SstUtils.getEntry(idToInfo, id.value,
                info -> gson.fromJson(info, CommentInfo.class));
    }

    @Override
    public boolean updateComment(CommentId id, CommentInfo commentInfo) {
        if (!SstUtils.contains(idToInfo, id.value)) return false;
        return SstUtils.setEntry(idToInfo, id.value, gson.toJson(commentInfo));
    }

    @Override
    public void deleteComment(CommentId id) {
        SstUtils.removeEntryWithKey(idToInfo, id.value);
    }

    @Override
    public void shutdown() {
        SstUtils.waitFuture(idToInfo.shutdown());
    }

    public static CommentRepository get(Path storingPath) {
        return new CommentSstRepository(storingPath);
    }
}
