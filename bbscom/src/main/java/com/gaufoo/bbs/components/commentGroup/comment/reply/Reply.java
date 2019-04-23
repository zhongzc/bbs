package com.gaufoo.bbs.components.commentGroup.comment.reply;

import com.gaufoo.bbs.components.commentGroup.comment.reply.common.ReplyId;
import com.gaufoo.bbs.components.commentGroup.comment.reply.common.ReplyInfo;
import com.gaufoo.bbs.components.idGenerator.IdGenerator;

import java.util.Optional;

public interface Reply {
    Optional<ReplyId> reply(ReplyInfo replyInfo);

    Optional<ReplyInfo> replyInfo(ReplyId replyId);

    boolean removeReply(ReplyId replyId);

    static Reply defau1t(IdGenerator idGenerator, ReplyRepository replyRepository) {
        return new ReplyImpl(idGenerator, replyRepository);
    }
}
