package com.gaufoo.bbs.components.commentGroup;

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

public interface CommentGroup {
    // 构造一个评论句柄
    Optional<CommentGroupId> cons();

    Optional<CommentId> addComment(CommentGroupId commentGroupId, CommentInfo commentInfo);

    Optional<ReplyId> addReply(CommentGroupId commentGroupId, CommentId commentId, ReplyInfo replyInfo);

    Optional<CommentInfo> commentInfo(CommentId commentId);

    Optional<ReplyInfo> replyInfo(ReplyId replyId);

    boolean removeComments(CommentGroupId commentGroupId);

    boolean removeComment(CommentGroupId commentGroupId, CommentId commentId);

    boolean removeReply(CommentId commentId, ReplyId replyId);

    Long getCommentsCount(CommentGroupId commentGroupId);

    Long getRepliesCount(CommentId commentId);

    Stream<CommentId> allComments(CommentGroupId commentGroupId);

    Stream<ReplyId> allReplies(CommentId commentId);

    static CommentGroup defau1t(IdGenerator cmmtGpIds, IdGenerator cmmtIds, IdGenerator rpyIds, CommentGroupRepository cmmtGpRepository, CommentRepository cmmtRepository, ReplyRepository rpyRepository) {
        return new CommentGroupImpl(cmmtGpIds, cmmtIds, rpyIds, cmmtGpRepository, cmmtRepository, rpyRepository);
    }
}
