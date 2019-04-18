package com.gaufoo.bbs.components.comment.reply;

import com.gaufoo.bbs.components.comment.reply.common.ReplyId;
import com.gaufoo.bbs.components.comment.reply.common.ReplyInfo;
import com.gaufoo.bbs.components.idGenerator.IdGenerator;

import java.util.Optional;

public interface Reply {
    Optional<ReplyId> reply(ReplyInfo replyInfo);

    Optional<ReplyInfo> replyInfo(ReplyId replyId);

    void removeReply(ReplyId replyId);

    void shutdown();

    static Reply defau1t(IdGenerator idGenerator, ReplyRepository replyRepository) {
        return new ReplyImpl(idGenerator, replyRepository);
    }
}
