package com.gaufoo.bbs.components.comment.reply;

import com.gaufoo.bbs.components.comment.reply.common.ReplyId;
import com.gaufoo.bbs.components.comment.reply.common.ReplyInfo;

public interface ReplyRepository {
    boolean saveReply(ReplyId id, ReplyInfo replyInfo);

    ReplyInfo getReply(ReplyId id);

    boolean updateReply(ReplyId id, ReplyInfo replyInfo);

    void deleteReply(ReplyId id);

    default void shutdown() {}

    String getRepositoryName();
}
