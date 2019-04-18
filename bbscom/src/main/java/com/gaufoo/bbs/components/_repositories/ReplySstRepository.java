package com.gaufoo.bbs.components._repositories;

import com.gaufoo.bbs.components.comment.reply.ReplyRepository;
import com.gaufoo.bbs.components.comment.reply.common.ReplyId;
import com.gaufoo.bbs.components.comment.reply.common.ReplyInfo;
import com.gaufoo.bbs.util.SstUtils;
import com.gaufoo.sst.SST;
import com.google.gson.Gson;

import java.nio.file.Path;

public class ReplySstRepository implements ReplyRepository {
    private static final Gson gson = new Gson();
    private final String repositoryName;
    private final SST idToInfo;

    public ReplySstRepository(String repositoryName, Path storingPath) {
        this.repositoryName = repositoryName;
        this.idToInfo = SST.of(repositoryName, storingPath.resolve(repositoryName));
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

    @Override
    public String getRepositoryName() {
        return this.repositoryName;
    }

    public static ReplyRepository get(String repositoryName, Path storingPath) {
        return new ReplySstRepository(repositoryName, storingPath);
    }
}
