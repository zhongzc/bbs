package com.gaufoo.bbs.components.commentGroup.comment;

import com.gaufoo.bbs.components.commentGroup.comment.reply.Reply;
import com.gaufoo.bbs.components.commentGroup.comment.reply.ReplyRepository;
import com.gaufoo.bbs.components.commentGroup.comment.reply.common.ReplyId;
import com.gaufoo.bbs.components.commentGroup.comment.reply.common.ReplyInfo;
import com.gaufoo.bbs.components.idGenerator.IdGenerator;
import com.gaufoo.bbs.components.commentGroup.comment.common.CommentId;
import com.gaufoo.bbs.components.commentGroup.comment.common.CommentInfo;

import java.util.Optional;

public class CommentImpl implements Comment {
    private final IdGenerator cmmtIds;
    private final CommentRepository repository;
    private final Reply reply;

    CommentImpl(IdGenerator cmmtIds, IdGenerator rpyIds, CommentRepository commentRepository, ReplyRepository replyRepository) {
        this.cmmtIds = cmmtIds;
        this.repository = commentRepository;
        this.reply = Reply.defau1t(rpyIds, replyRepository);
    }

    @Override
    public Optional<CommentId> comment(CommentInfo commentInfo) {
        CommentId id = CommentId.of(cmmtIds.generateId());

        if (repository.saveComment(id, commentInfo)) {
            return Optional.of(id);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Optional<ReplyId> reply(CommentId commentId, ReplyInfo replyInfo) {
        return Optional.ofNullable(repository.getComment(commentId)).flatMap(info ->
                reply.reply(replyInfo).flatMap(rpyId -> {
                    info.replies.add(rpyId);
                    if (repository.updateComment(commentId, info.modReplies(info.replies)))
                        return Optional.of(rpyId);
                    else
                        return Optional.empty();
                }));
    }

    @Override
    public Optional<CommentInfo> commentInfo(CommentId commentId) {
        return Optional.ofNullable(repository.getComment(commentId));
    }

    @Override
    public Optional<ReplyInfo> replyInfo(ReplyId replyId) {
        return this.reply.replyInfo(replyId);
    }

    @Override
    public void removeComment(CommentId commentId) {
        repository.deleteComment(commentId);
    }

    @Override
    public void removeReply(CommentId commentId, ReplyId replyId) {
        Optional.ofNullable(repository.getComment(commentId)).ifPresent(info -> {
            info.replies.remove(replyId);
            repository.updateComment(commentId,
                    info.modReplies(info.replies));
        });
        reply.removeReply(replyId);
    }

}
