package com.gaufoo.bbs.components.commentGroup;

import com.gaufoo.bbs.components.commentGroup.comment.Comment;
import com.gaufoo.bbs.components.commentGroup.comment.CommentRepository;
import com.gaufoo.bbs.components.commentGroup.comment.common.CommentId;
import com.gaufoo.bbs.components.commentGroup.comment.common.CommentInfo;
import com.gaufoo.bbs.components.commentGroup.comment.reply.ReplyRepository;
import com.gaufoo.bbs.components.commentGroup.comment.reply.common.ReplyId;
import com.gaufoo.bbs.components.commentGroup.comment.reply.common.ReplyInfo;
import com.gaufoo.bbs.components.commentGroup.common.CommentGroupId;
import com.gaufoo.bbs.components.idGenerator.IdGenerator;

import java.util.Optional;
import java.util.stream.Stream;

public class CommentGroupImpl implements CommentGroup {
    private final CommentGroupRepository repository;
    private final Comment comment;
    private final IdGenerator idGenerator;

    CommentGroupImpl(IdGenerator cmmtGpIds, IdGenerator cmmtIds, IdGenerator rpyIds, CommentGroupRepository cmmtGpRepository, CommentRepository cmmtRepository, ReplyRepository rpyRepository) {
        this.repository = cmmtGpRepository;
        this.comment = Comment.defau1t(cmmtIds, rpyIds, cmmtRepository, rpyRepository);
        this.idGenerator = cmmtGpIds;
    }

    @Override
    public Optional<CommentGroupId> cons() {
        CommentGroupId id = CommentGroupId.of(idGenerator.generateId());
        if (repository.cons(id)) {
            return Optional.of(id);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Optional<CommentId> addComment(CommentGroupId commentGroupId, CommentInfo commentInfo) {
        return comment.comment(commentInfo).flatMap(id -> {
            if (repository.addComment(commentGroupId, id)) {
                return Optional.of(id);
            } else {
                return Optional.empty();
            }
        });
    }

    @Override
    public Optional<ReplyId> addReply(CommentGroupId commentGroupId, CommentId commentId, ReplyInfo replyInfo) {
        return comment.reply(commentId, replyInfo);
    }

    @Override
    public Optional<CommentInfo> commentInfo(CommentId commentId) {
        return comment.commentInfo(commentId);
    }

    @Override
    public Optional<ReplyInfo> replyInfo(ReplyId replyId) {
        return comment.replyInfo(replyId);
    }

    @Override
    public void removeComments(CommentGroupId commentGroupId) {
        repository.getAllComments(commentGroupId).forEach(comment::removeComment);
        repository.deleteComments(commentGroupId);
    }

    @Override
    public void removeComment(CommentGroupId commentGroupId, CommentId commentId) {
        repository.deleteComment(commentGroupId, commentId);
        comment.removeComment(commentId);
    }

    @Override
    public void removeReply(CommentGroupId commentGroupId, CommentId commentId, ReplyId replyId) {
        comment.removeReply(commentId, replyId);
    }

    @Override
    public Long getCommentsCount(CommentGroupId commentGroupId) {
        return Optional.ofNullable(repository.getCommentsCount(commentGroupId)).orElse(0L);
    }

    public Stream<CommentId> allComments(CommentGroupId commentGroupId) {
        return repository.getAllComments(commentGroupId);
    }
}
