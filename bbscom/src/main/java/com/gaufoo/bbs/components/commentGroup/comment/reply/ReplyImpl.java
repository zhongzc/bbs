package com.gaufoo.bbs.components.commentGroup.comment.reply;

import com.gaufoo.bbs.components.commentGroup.comment.reply.common.ReplyId;
import com.gaufoo.bbs.components.commentGroup.comment.reply.common.ReplyInfo;
import com.gaufoo.bbs.components.idGenerator.IdGenerator;

import java.util.Optional;

public class ReplyImpl implements Reply {
    private final IdGenerator idGenerator;
    private final ReplyRepository repository;

    ReplyImpl(IdGenerator idGenerator, ReplyRepository replyRepository) {
        this.idGenerator = idGenerator;
        this.repository = replyRepository;
    }

    @Override
    public Optional<ReplyId> reply(ReplyInfo replyInfo) {
        ReplyId id = ReplyId.of(idGenerator.generateId());

        if (repository.saveReply(id, replyInfo)) {
            return Optional.of(id);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Optional<ReplyInfo> replyInfo(ReplyId replyId) {
        return Optional.ofNullable(repository.getReply(replyId));
    }

    @Override
    public boolean removeReply(ReplyId replyId) {
        return repository.deleteReply(replyId);
    }

}
