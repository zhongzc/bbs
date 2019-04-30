package com.gaufoo.bbs.application;

import static com.gaufoo.bbs.application.types.Comment.*;

import com.gaufoo.bbs.application.error.ErrorCode;
import com.gaufoo.bbs.application.types.Comment;
import com.gaufoo.bbs.application.types.Content;
import static com.gaufoo.bbs.application.types.PersonalInformation.*;
import com.gaufoo.bbs.components.commentGroup.CommentGroup;
import com.gaufoo.bbs.components.commentGroup.comment.common.CommentId;
import com.gaufoo.bbs.components.commentGroup.comment.common.CommentInfo;
import com.gaufoo.bbs.components.commentGroup.comment.reply.common.ReplyId;
import com.gaufoo.bbs.components.commentGroup.comment.reply.common.ReplyInfo;
import com.gaufoo.bbs.components.commentGroup.common.CommentGroupId;
import com.gaufoo.bbs.components.content.common.ContentId;
import com.gaufoo.bbs.components.user.common.UserId;
import com.gaufoo.bbs.util.TaskChain;
import com.gaufoo.bbs.util.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.gaufoo.bbs.application.ComponentFactory.componentFactory;
import static com.gaufoo.bbs.application.AppContent.*;
import static com.gaufoo.bbs.application.Commons.*;

public class AppComment {
    public static Logger log = LoggerFactory.getLogger(AppComment.class);

    public static TaskChain.Procedure<ErrorCode, CommentGroupId> createCommentGroup() {
        return TaskChain.Procedure.fromOptional(componentFactory.commentGroup.cons(), ErrorCode.CreateCommentGroupFailed)
                .then(commentGroupId -> TaskChain.Result.of(commentGroupId, () -> componentFactory.commentGroup.removeComments(commentGroupId)));
    }

    public static TaskChain.Procedure<ErrorCode, Tuple<CommentId, CommentInfo>> postComment(CommentGroupId groupId, ContentId contentId,
                                                                                            UserId commenterId) {
        return TaskChain.Result.<ErrorCode, CommentInfo>of(CommentInfo.of(contentId.value, commenterId.value))
                .then(commentInfo -> TaskChain.Procedure.fromOptional(
                        componentFactory.commentGroup.addComment(groupId, commentInfo).map(commentId -> Tuple.of(commentId, commentInfo)),
                        ErrorCode.AddCommentFailed)
                );
    }

    public static TaskChain.Procedure<ErrorCode, Tuple<ReplyId, ReplyInfo>> postReply(CommentGroupId groupId, CommentId commentId, ContentId contentId,
                                                                                      UserId replier, UserId replyTo) {
        return TaskChain.Result.<ErrorCode, ReplyInfo>of(ReplyInfo.of(replier.value, contentId.value, replyTo.value))
                .then(replyInfo -> TaskChain.Procedure.fromOptional(
                        componentFactory.commentGroup.addReply(groupId, commentId, replyInfo).map(replyId -> Tuple.of(replyId, replyInfo)),
                        ErrorCode.AddReplyFailed)
                );
    }

    public static TaskChain.Procedure<ErrorCode, CommentInfo> fetchCommentInfo(CommentId commentId) {
        return TaskChain.Procedure.fromOptional(componentFactory.commentGroup.commentInfo(commentId),
                ErrorCode.CommentInfoNotFound);
    }

    public static TaskChain.Procedure<ErrorCode, ReplyInfo> fetchReplyInfo(ReplyId replyId) {
        return TaskChain.Procedure.fromOptional(componentFactory.commentGroup.replyInfo(replyId),
                ErrorCode.ReplyInfoNotFound);
    }

    public static TaskChain.Procedure<ErrorCode, Void> deleteAllComments(CommentGroupId groupId) {
        return TaskChain.Procedure.sequence(componentFactory.commentGroup.allComments(groupId)
                .map(commentId -> deleteComment(groupId, commentId))
                .collect(Collectors.toList())
        ).then(__ -> componentFactory.commentGroup.removeComments(groupId) ?
                TaskChain.Result.of(null) :
                TaskChain.Fail.of(ErrorCode.DeleteCommentFailed)
        );
    }

    public static TaskChain.Procedure<ErrorCode, Void> deleteComment(CommentGroupId commentGroupId, CommentId commentId) {
        Supplier<TaskChain.Procedure<ErrorCode, Void>> clearUp = () -> TaskChain.Procedure.sequence(
                componentFactory.commentGroup.allReplies(commentId)
                        .map(replyId -> deleteReply(commentId, replyId)).collect(Collectors.toList())
        ).mapR(__ -> null);

        return componentFactory.commentGroup.commentInfo(commentId)
                .map(commentInfo -> componentFactory.content.remove(ContentId.of(commentInfo.contentId)))
                .filter(ok -> ok)
                .map(__ -> componentFactory.commentGroup.removeComment(commentGroupId, commentId))
                .filter(ok -> ok)
                .map(__ -> clearUp.get())
                .orElse(TaskChain.Fail.of(ErrorCode.DeleteCommentFailed));
    }

    public static TaskChain.Procedure<ErrorCode, Void> deleteReply(CommentId commentId, ReplyId replyId) {
        return componentFactory.commentGroup.replyInfo(replyId)
                .map(replyInfo -> componentFactory.content.remove(ContentId.of(replyInfo.contentId)))
                .filter(ok -> ok)
                .map(__ -> componentFactory.commentGroup.removeReply(commentId, replyId))
                .filter(ok -> ok)
                .map(__ -> (TaskChain.Procedure<ErrorCode, Void>)TaskChain.Result.<ErrorCode, Void>of(null))
                .orElse(TaskChain.Fail.of(ErrorCode.DeleteReplyFailed));
    }

    public static AllComments consAllComments(CommentGroupId commentGroupId, Long skip, Long first) {
        final CommentGroup cg = componentFactory.commentGroup;
        return new AllComments() {
            public Long              getTotalCount() { return cg.getCommentsCount(commentGroupId); }
            public List<Comment.CommentInfo> getComments()   {
                return cg.allComments(commentGroupId).map(AppComment::consCommentInfo)
                        .filter(Objects::nonNull).skip(skip == null ? 0L : skip)
                        .limit(first == null ? Long.MAX_VALUE : first)
                        .collect(Collectors.toList());
            }
        };
    }

    public static Comment.CommentInfo consCommentInfo(CommentId cid) {
        final CommentGroup cg = componentFactory.commentGroup;
        return cg.commentInfo(cid).map(cinfo -> new Comment.CommentInfo() {
            public String       getId()      { return cid.value; }
            public Content      getContent() { return fromContentId(ContentId.of(cinfo.contentId)).reduce(AppComment::warnNil, i -> i); }
            public PersonalInfo getAuthor()  { return fetchPersonalInfo(UserId.of(cinfo.commenter)).reduce(AppComment::warnNil, i -> i); }
            public AllReplies   getAllReplies(Long skip, Long first) { return consAllReplies(cid, skip, first); }
            public Long getCreationTime()    { return cinfo.creationTime.toEpochMilli(); }
        }).orElse(null);
    }

    public static Comment.AllReplies consAllReplies(CommentId cid, Long skip, Long first) {
        final CommentGroup cg = componentFactory.commentGroup;
        return new Comment.AllReplies() {
            public Long getTotalCount() { return cg.getRepliesCount(cid); }
            public List<Comment.ReplyInfo> getReplies() {
                return cg.allReplies(cid).map(AppComment::consReplyInfo)
                        .filter(Objects::nonNull).skip(skip == null ? 0L : skip)
                        .limit(first == null ? Long.MAX_VALUE : first)
                        .collect(Collectors.toList());
            }
        };
    }

    public static Comment.ReplyInfo consReplyInfo(ReplyId rid) {
        final CommentGroup cg = componentFactory.commentGroup;
        return cg.replyInfo(rid).map(rinfo ->
            new Comment.ReplyInfo() {
                public String       getId()      { return rid.value; }
                public Content      getContent() { return fromContentId(ContentId.of(rinfo.contentId)).reduce(AppComment::warnNil, i -> i); }
                public PersonalInfo getAuthor()  { return fetchPersonalInfo(UserId.of(rinfo.replier)).reduce(AppComment::warnNil, i -> i); }
                public PersonalInfo getReplyTo() { return Optional.ofNullable(rinfo.replyTo).map(rpt -> fetchPersonalInfo(UserId.of(rpt)).reduce(AppComment::warnNil, i -> i)).orElse(null); }
                public Long getCreationTime()    { return rinfo.creationTime.toEpochMilli(); }
            }).orElse(null);
    }

    private static <T, E> T warnNil(E error) {
        log.warn("null warning: {}", error);
        return null;
    }
}
