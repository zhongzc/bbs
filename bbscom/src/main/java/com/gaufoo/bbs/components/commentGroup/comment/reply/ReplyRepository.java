package com.gaufoo.bbs.components.commentGroup.comment.reply;

import com.gaufoo.bbs.components.commentGroup.comment.reply.common.ReplyId;
import com.gaufoo.bbs.components.commentGroup.comment.reply.common.ReplyInfo;

public interface ReplyRepository {
    boolean saveReply(ReplyId id, ReplyInfo replyInfo);

    ReplyInfo getReply(ReplyId id);

    boolean deleteReply(ReplyId id);

    default void shutdown() {}
}
