package com.gaufoo.bbs.components.commentGroup.comment.reply;

import com.gaufoo.bbs.components.commentGroup.comment.reply.common.ReplyId;
import com.gaufoo.bbs.components.commentGroup.comment.reply.common.ReplyInfo;
import com.gaufoo.bbs.util.SstUtils;
import com.gaufoo.sst.SST;
import com.google.gson.Gson;

import java.nio.file.Path;

public class ReplySstRepository implements ReplyRepository {
    private static final Gson gson = new Gson();
    private final SST idToInfo;

    public ReplySstRepository(Path storingPath) {
        this.idToInfo = SST.of("id-to-info", storingPath);
    }

    @Override
    public boolean saveReply(ReplyId id, ReplyInfo replyInfo) {
        if (SstUtils.contains(idToInfo, id.value)) return false;
        return SstUtils.setEntry(idToInfo, id.value, gson.toJson(replyInfo));
    }

    @Override
    public ReplyInfo getReply(ReplyId id) {
        return SstUtils.getEntry(idToInfo, id.value,
                info -> gson.fromJson(info, ReplyInfo.class));
    }

    @Override
    public boolean updateReply(ReplyId id, ReplyInfo replyInfo) {
        if (!SstUtils.contains(idToInfo, id.value)) return false;
        return SstUtils.setEntry(idToInfo, id.value, gson.toJson(replyInfo));
    }

    @Override
    public void deleteReply(ReplyId id) {
        SstUtils.removeEntryWithKey(idToInfo, id.value);
    }

    @Override
    public void shutdown() {
        SstUtils.waitFuture(idToInfo.shutdown());
    }

    public static ReplyRepository get(Path storingPath) {
        return new ReplySstRepository(storingPath);
    }
}
