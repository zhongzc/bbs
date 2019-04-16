package com.gaufoo.bbs.components.reply;

import com.gaufoo.bbs.components.reply.common.CommentId;
import com.gaufoo.bbs.components.reply.common.CommentInfo;

public interface CommentRepository {
    boolean saveComment(CommentId id, CommentInfo commentInfo);

    CommentInfo getComment(CommentId id);

    boolean updateComment(CommentId id, CommentInfo commentInfo);

    void deleteComment(CommentId id);

    default void shutdown() {}

    String getRepositoryName();
}
