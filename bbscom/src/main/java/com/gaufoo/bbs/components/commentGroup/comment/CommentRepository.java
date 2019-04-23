package com.gaufoo.bbs.components.commentGroup.comment;

import com.gaufoo.bbs.components.commentGroup.comment.common.CommentId;
import com.gaufoo.bbs.components.commentGroup.comment.common.CommentInfo;
import com.gaufoo.bbs.components.commentGroup.comment.reply.common.ReplyId;

import java.util.stream.Stream;

public interface CommentRepository {
    boolean saveComment(CommentId id, CommentInfo commentInfo);

    CommentInfo getComment(CommentId id);

    boolean addReply(CommentId commentId, ReplyId replyId);

    Stream<ReplyId> getAllReplies(CommentId id);

    Long getRepliesCount(CommentId id);

    boolean deleteComment(CommentId id);

    boolean deleteReply(CommentId commentId, ReplyId replyId);

    default void shutdown() {}

}
