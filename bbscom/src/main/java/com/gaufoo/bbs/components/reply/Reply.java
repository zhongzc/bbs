package com.gaufoo.bbs.components.reply;

import com.gaufoo.bbs.components.idGenerator.IdGenerator;
import com.gaufoo.bbs.components.reply.common.ReplyId;
import com.gaufoo.bbs.components.reply.common.ReplyInfo;

import java.util.Optional;

public interface Reply {
    Optional<ReplyId> reply(ReplyInfo replyInfo);

    boolean comment(ReplyId replyId, ReplyInfo.Comment comment);

    Optional<ReplyInfo> replyInfo(ReplyId replyId);

    void removeReply(ReplyId replyId);

    String getName();

    static Reply defau1t(String componentName, IdGenerator idGenerator, ReplyRepository replyRepository) {
        return new ReplyImpl(componentName, idGenerator, replyRepository);
    }
}
