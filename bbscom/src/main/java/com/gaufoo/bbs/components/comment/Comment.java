package com.gaufoo.bbs.components.comment;

import com.gaufoo.bbs.components.comment.reply.ReplyRepository;
import com.gaufoo.bbs.components.comment.reply.common.ReplyId;
import com.gaufoo.bbs.components.comment.reply.common.ReplyInfo;
import com.gaufoo.bbs.components.idGenerator.IdGenerator;
import com.gaufoo.bbs.components.comment.common.CommentId;
import com.gaufoo.bbs.components.comment.common.CommentInfo;

import java.util.Optional;

public interface Comment {
    Optional<CommentId> comment(CommentInfo commentInfo);

    Optional<ReplyId> reply(CommentId commentId, ReplyInfo reply);

    Optional<CommentInfo> commentInfo(CommentId commentId);

    Optional<ReplyInfo> replyInfo(ReplyId replyId);

    void removeComment(CommentId commentId);

    void removeReply(CommentId commentId, ReplyId replyId);

    String getName();

    void shutdown();

    static Comment defau1t(String componentName, IdGenerator cmmtIds, IdGenerator rpyIds, CommentRepository commentRepository, ReplyRepository replyRepository) {
        return new CommentImpl(componentName, cmmtIds, rpyIds, commentRepository, replyRepository);
    }
}
