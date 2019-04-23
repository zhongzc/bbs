package com.gaufoo.bbs.components.commentGroup.comment;

import com.gaufoo.bbs.components.commentGroup.comment.reply.Reply;
import com.gaufoo.bbs.components.commentGroup.comment.reply.ReplyRepository;
import com.gaufoo.bbs.components.commentGroup.comment.reply.common.ReplyId;
import com.gaufoo.bbs.components.commentGroup.comment.reply.common.ReplyInfo;
import com.gaufoo.bbs.components.idGenerator.IdGenerator;
import com.gaufoo.bbs.components.commentGroup.comment.common.CommentId;
import com.gaufoo.bbs.components.commentGroup.comment.common.CommentInfo;

import java.util.Optional;
import java.util.stream.Stream;

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
        return reply.reply(replyInfo).flatMap(rpyId -> {
            if (repository.addReply(commentId, rpyId)) {
                return Optional.of(rpyId);
            } else return Optional.empty();
        });
    }

    @Override
    public Optional<CommentInfo> commentInfo(CommentId commentId) {
        return Optional.ofNullable(repository.getComment(commentId));
    }

    @Override
    public Optional<ReplyInfo> replyInfo(ReplyId replyId) {
        return reply.replyInfo(replyId);
    }

    @Override
    public Stream<ReplyId> allReplies(CommentId commentId) {
        return repository.getAllReplies(commentId);
    }

    @Override
    public Long getRepliesCount(CommentId commentId) {
        return Optional.ofNullable(repository.getRepliesCount(commentId)).orElse(0L);
    }

    @Override
    public boolean removeComment(CommentId commentId) {
        if (repository.deleteComment(commentId)) {
            return repository.getAllReplies(commentId).allMatch(reply::removeReply);
        } else return false;
    }

    @Override
    public boolean removeReply(CommentId commentId, ReplyId replyId) {
        if (repository.deleteReply(commentId, replyId)) {
            return reply.removeReply(replyId);
        } else return false;
    }

}
