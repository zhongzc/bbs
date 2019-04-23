package com.gaufoo.bbs.components.commentGroup.comment;

import com.gaufoo.bbs.components.commentGroup.comment.reply.ReplyRepository;
import com.gaufoo.bbs.components.commentGroup.comment.reply.common.ReplyId;
import com.gaufoo.bbs.components.commentGroup.comment.reply.common.ReplyInfo;
import com.gaufoo.bbs.components.idGenerator.IdGenerator;
import com.gaufoo.bbs.components.commentGroup.comment.common.CommentId;
import com.gaufoo.bbs.components.commentGroup.comment.common.CommentInfo;

import java.util.Optional;
import java.util.stream.Stream;

public interface Comment {
    Optional<CommentId> comment(CommentInfo commentInfo);

    Optional<ReplyId> reply(CommentId commentId, ReplyInfo reply);

    Optional<CommentInfo> commentInfo(CommentId commentId);

    Optional<ReplyInfo> replyInfo(ReplyId replyId);

    Stream<ReplyId> allReplies(CommentId commentId);

    Long getRepliesCount(CommentId commentId);

    boolean removeComment(CommentId commentId);

    boolean removeReply(CommentId commentId, ReplyId replyId);

    static Comment defau1t(IdGenerator cmmtIds, IdGenerator rpyIds, CommentRepository commentRepository, ReplyRepository replyRepository) {
        return new CommentImpl(cmmtIds, rpyIds, commentRepository, replyRepository);
    }
}
