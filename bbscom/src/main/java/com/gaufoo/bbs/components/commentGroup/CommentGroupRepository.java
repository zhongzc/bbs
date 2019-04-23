package com.gaufoo.bbs.components.commentGroup;

import com.gaufoo.bbs.components.commentGroup.comment.common.CommentId;
import com.gaufoo.bbs.components.commentGroup.common.CommentGroupId;

import java.util.stream.Stream;

public interface CommentGroupRepository {
    boolean cons(CommentGroupId commentGroupId);

    boolean addComment(CommentGroupId commentGroupId, CommentId commentId);

    Stream<CommentId> getAllComments(CommentGroupId commentGroupId);

    boolean deleteComments(CommentGroupId commentGroupId);

    boolean deleteComment(CommentGroupId commentGroupId, CommentId commentId);

    Long getCommentsCount(CommentGroupId commentGroupId);

    default void shutdown() { }
}
