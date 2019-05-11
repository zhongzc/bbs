package com.gaufoo.bbs.application;

import com.gaufoo.bbs.application.error.Error;
import com.gaufoo.bbs.application.error.ErrorCode;
import com.gaufoo.bbs.application.error.Ok;
import com.gaufoo.bbs.application.types.*;
import com.gaufoo.bbs.application.types.PersonalInformation;
import com.gaufoo.bbs.application.util.LazyVal;
import com.gaufoo.bbs.components.active.Active;
import com.gaufoo.bbs.components.active.common.ActiveInfo;
import com.gaufoo.bbs.components.authenticator.Authenticator;
import com.gaufoo.bbs.components.authenticator.common.Permission;
import com.gaufoo.bbs.components.authenticator.common.UserToken;
import com.gaufoo.bbs.components.commentGroup.CommentGroup;
import com.gaufoo.bbs.components.commentGroup.comment.common.CommentId;
import com.gaufoo.bbs.components.commentGroup.comment.common.CommentInfo;
import com.gaufoo.bbs.components.commentGroup.comment.reply.common.ReplyId;
import com.gaufoo.bbs.components.commentGroup.comment.reply.common.ReplyInfo;
import com.gaufoo.bbs.components.commentGroup.common.CommentGroupId;
import com.gaufoo.bbs.components.content.common.ContentId;
import com.gaufoo.bbs.components.content.common.ContentInfo;
import com.gaufoo.bbs.components.heat.Heat;
import com.gaufoo.bbs.components.schoolHeat.common.SchoolHeatId;
import com.gaufoo.bbs.components.schoolHeat.common.SchoolHeatInfo;

import static com.gaufoo.bbs.util.TaskChain.Procedure.*;
import static com.gaufoo.bbs.util.TaskChain.*;

import com.gaufoo.bbs.components.user.common.UserId;
import com.gaufoo.bbs.util.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.gaufoo.bbs.application.ComponentFactory.componentFactory;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AppSchoolHeat {
    public static Logger log = LoggerFactory.getLogger(SchoolHeat.class);
    private static final Consumer<ErrorCode> warnNil = errorCode -> log.warn("null warning: {}", errorCode);
    private static final String postGroupId = Commons.getGroupId(Commons.PostType.SchoolHeat);


    public static SchoolHeat.AllSchoolHeatsResult allSchoolHeats(Long nullableSkip, Long nullableFirst, Commons.SortedBy nullableSortedBy) {
        final long skip = nullableSkip == null ? 0L : nullableSkip;
        final long first = nullableFirst == null ? Long.MAX_VALUE : nullableFirst;
        final Commons.SortedBy sortedBy = nullableSortedBy == null ? Commons.SortedBy.ActiveTimeDes : nullableSortedBy;

        Supplier<Long> totalCount = () -> componentFactory.schoolHeat.allPostsCount();

        Stream<SchoolHeat.SchoolHeatInfo> infoStream = selectSchoolHeats(sortedBy)
                .map(id -> Tuple.of(id, LazyVal.of(() -> fetchSchoolHeatInfoAndUnwrap(id, warnNil))))
                .map(idInfoTup -> consSchoolHeatInfoRet(idInfoTup.left, idInfoTup.right));

        return consMultiSchoolHeats(totalCount, infoStream, skip, first);
    }

    public static SchoolHeat.SchoolHeatInfoResult schoolHeatInfo(String id) {
        SchoolHeatId schoolHeatId = SchoolHeatId.of(id);

        return fetchSchoolHeatInfo(schoolHeatId)
                .reduce(Error::of, info -> consSchoolHeatInfoRet(schoolHeatId, LazyVal.with(info)));
    }

    public static SchoolHeat.SchoolHeatsOfAuthorResult schoolHeatsOfAuthor(String userId, Long skip, Long first) {
        final long fSkip = skip == null ? 0L : skip;
        final long fFirst = first == null ? Long.MAX_VALUE : first;

        Supplier<Long> totalCount = () -> componentFactory.schoolHeat.allPostsCountByAuthor(userId);

        Stream<SchoolHeat.SchoolHeatInfo> infoStream = componentFactory.schoolHeat.allPostsByAuthor(userId)
                .map(id -> Tuple.of(id, LazyVal.of(() -> fetchSchoolHeatInfoAndUnwrap(id, warnNil))))
                .map(idInfoTup -> consSchoolHeatInfoRet(idInfoTup.left, idInfoTup.right));

        return consMultiSchoolHeats(totalCount, infoStream, fSkip, fFirst);
    }

    public static SchoolHeat.CreateSchoolHeatResult createSchoolHeat(SchoolHeat.SchoolHeatInput input, String loginToken) {
        class Ctx {
            UserId userId;                 Void put(UserId id)         { this.userId = id;         return null; }
            ContentId contentId;           Void put(ContentId id)      { this.contentId = id;      return null; }
            CommentGroupId commentGroupId; Void put(CommentGroupId id) { this.commentGroupId = id; return null; }
            SchoolHeatId schoolHeatId;     Void put(SchoolHeatId id)   { this.schoolHeatId = id;   return null; }
            SchoolHeatInfo schoolHeatInfo; Void put(SchoolHeatInfo i)  { this.schoolHeatInfo = i;  return null; }
        } Ctx ctx = new Ctx();

        return Commons.fetchUserId(UserToken.of(loginToken)).mapR(ctx::put)
                .then(__ -> AppContent.storeContentInput(input.content)).mapR(ctx::put)
                .then(__ -> AppComment.createCommentGroup()).mapR(ctx::put)
                .mapR(__ -> SchoolHeatInfo.of(input.title, ctx.contentId.value, ctx.userId.value, ctx.commentGroupId.value)).mapR(ctx::put)
                .then(__ -> publishSchoolHeat(ctx.schoolHeatInfo)).mapR(ctx::put)
                .then(__ -> AppHeatActive.createActiveAndHeat(ctx.schoolHeatId, ctx.userId))
                .reduce(Error::of, __ -> consSchoolHeatInfoAfterCreate(ctx.schoolHeatId, input, ctx.contentId, ctx.userId, ctx.schoolHeatInfo));
    }

    public static SchoolHeat.DeleteSchoolHeatResult deleteSchoolHeat(String id, String token) {
        SchoolHeatId schoolHeatId = SchoolHeatId.of(id);
        class Ctx {
            SchoolHeatInfo schoolHeatInfo; Void put(SchoolHeatInfo i) { schoolHeatInfo = i; return null; }
        } Ctx ctx = new Ctx();

        return Commons.fetchPermission(UserToken.of(token))
                .then(permission -> checkPermission(schoolHeatId, permission)).mapR(ctx::put)
                .then(__ -> deleteSchoolHeatInfo(schoolHeatId, ctx.schoolHeatInfo))
                .mapR(__ -> AppHeatActive.clearActiveAndHeat(schoolHeatId))
                .reduce(Error::of, __ -> Ok.build());
    }

    public static SchoolHeat.CreateSchoolHeatCommentResult createSchoolHeatComment(SchoolHeat.SchoolHeatCommentInput input, String userToken) {
        SchoolHeatId schoolHeatId = SchoolHeatId.of(input.postIdCommenting);

        class Ctx {
            UserId userId;                       Void put(UserId id)    { userId = id;           return null; }
            ContentId contentId;                 Void put(ContentId id) { contentId = id;        return null; }
            SchoolHeatInfo schoolHeatInfo; Void put(SchoolHeatInfo i)   { schoolHeatInfo = i; return null; }
            CommentId commentId;
            CommentInfo commentInfo; Void put(Tuple<CommentId, CommentInfo> tup) { commentId = tup.left; commentInfo = tup.right; return null; }
        } Ctx ctx = new Ctx();

        return Commons.fetchUserId(UserToken.of(userToken)).mapR(ctx::put)
                .then(__ -> fetchSchoolHeatInfo(schoolHeatId)).mapR(ctx::put)
                .then(__ -> AppContent.storeContentInput(input.content)).mapR(ctx::put)
                .mapR(__ -> CommentInfo.of(ctx.contentId.value, ctx.userId.value))
                .then(__ -> AppComment.postComment(CommentGroupId.of(ctx.schoolHeatInfo.commentGroupId), ctx.contentId, ctx.userId)).mapR(ctx::put)
                .then(__ -> AppHeatActive.alterHeat(schoolHeatId, ctx.schoolHeatInfo.createTime, 1))
                .then(__ -> AppHeatActive.touchActive(schoolHeatId, ctx.userId))
                .reduce(Error::of, __ -> AppComment.consCommentInfoInstant(ctx.commentId, ctx.contentId, ctx.userId, ctx.commentInfo));
    }

    public static SchoolHeat.DeleteSchoolHeatCommentResult deleteSchoolHeatComment(String schoolHeatIdStr, String commentIdStr, String userToken) {
        SchoolHeatId schoolHeatId = SchoolHeatId.of(schoolHeatIdStr);
        CommentId commentId = CommentId.of(commentIdStr);

        class Ctx {
            SchoolHeatInfo schoolHeatInfo; Void put(SchoolHeatInfo i) { schoolHeatInfo = i; return null; }
        } Ctx ctx = new Ctx();

        return Commons.fetchPermission(UserToken.of(userToken))
                .then(permission -> checkPermission(commentId, permission))
                .then(__ -> fetchSchoolHeatInfo(schoolHeatId)).mapR(ctx::put)
                .then(__ -> AppHeatActive.alterHeat(schoolHeatId, ctx.schoolHeatInfo.createTime, -1))
                .then(__ -> AppComment.deleteComment(CommentGroupId.of(ctx.schoolHeatInfo.commentGroupId), commentId))
                .reduce(Error::of, __ -> Ok.build());
    }

    public static SchoolHeat.CreateSchoolHeatCommentReplyResult createSchoolHeatCommentReply(SchoolHeat.SchoolHeatReplyInput input, String userToken) {
        SchoolHeatId schoolHeatId = SchoolHeatId.of(input.postIdReplying);
        UserId nullableReplyTo = nilOrTr(input.replyTo, UserId::of);

        class Ctx {
            UserId userId; Void put(UserId id) { userId = id; return null; }
            ContentId contentId; Void put(ContentId id) {contentId = id; return null;}
            SchoolHeatInfo schoolHeatInfo; Void put(SchoolHeatInfo i) { schoolHeatInfo = i; return null; }
            ReplyId replyId;
            ReplyInfo replyInfo; Void put(Tuple<ReplyId, ReplyInfo> tup) { replyId = tup.left; replyInfo = tup.right; return null; }
        } Ctx ctx = new Ctx();

        return Commons.fetchUserId(UserToken.of(userToken)).mapR(ctx::put)
                .then(__ -> fetchSchoolHeatInfo(schoolHeatId)).mapR(ctx::put)
                .then(__ -> nullableReplyTo == null ? Result.of(null) : Commons.ensureUserExist(nullableReplyTo))
                .then(__ -> AppContent.storeContentInput(input.content)).mapR(ctx::put)
                .then(__ -> AppComment.postReply(
                        CommentGroupId.of(ctx.schoolHeatInfo.commentGroupId), CommentId.of(input.commentIdReplying), ctx.contentId, ctx.userId, nullableReplyTo)).mapR(ctx::put)
                .then(__ -> AppHeatActive.alterHeat(schoolHeatId, ctx.schoolHeatInfo.createTime, 1))
                .then(__ -> AppHeatActive.touchActive(schoolHeatId, ctx.userId))
                .reduce(Error::of, __ -> AppComment.consReplyInfoInstant(ctx.replyId, ctx.replyInfo, ctx.contentId, ctx.userId));
    }

    public static SchoolHeat.DeleteSchoolHeatCommentReplyResult deleteSchoolHeatCommentReply(String schoolHeatIdStr, String commentIdStr, String replyIdStr, String userToken) {
        SchoolHeatId schoolHeatId = SchoolHeatId.of(schoolHeatIdStr);
        CommentId commentId = CommentId.of(commentIdStr);
        ReplyId replyId = ReplyId.of(replyIdStr);

        return Commons.fetchPermission(UserToken.of(userToken))
                .then(permission -> checkPermission(replyId, permission))
                .then(__ -> fetchSchoolHeatInfo(schoolHeatId))
                .then(enInfo -> AppHeatActive.alterHeat(schoolHeatId, enInfo.createTime, -1))
                .then(__ -> AppComment.deleteReply(commentId, replyId))
                .reduce(Error::of, __ -> Ok.build());
    }

    private static Procedure<ErrorCode, Void> deleteSchoolHeatInfo(SchoolHeatId schoolHeatId, SchoolHeatInfo schoolHeatInfo) {
        return AppContent.deleteContent(ContentId.of(schoolHeatInfo.contentId))
                .then(__ -> AppComment.deleteAllComments(CommentGroupId.of(schoolHeatInfo.commentGroupId)))
                .mapR(__ -> componentFactory.schoolHeat.removePost(schoolHeatId))
                .then(ok -> ok ? Result.of(null) : Fail.of(ErrorCode.DeletePostFailed));
    }

    private static Procedure<ErrorCode, SchoolHeatInfo> checkPermission(SchoolHeatId schoolHeatId, Permission permission) {
        class Ctx {
            SchoolHeatInfo schoolHeatInfo; Void put(SchoolHeatInfo i) { schoolHeatInfo = i; return null; }
        } Ctx ctx = new Ctx();

        return Procedure.fromOptional(componentFactory.schoolHeat.postInfo(schoolHeatId), ErrorCode.PostNonExist).mapR(ctx::put)
                .mapR(__ -> ctx.schoolHeatInfo.authorId.equals(permission.userId) || permission.role.equals(Authenticator.Role.ADMIN))
                .then(ok -> ok ? Result.of(ctx.schoolHeatInfo) : Fail.of(ErrorCode.PermissionDenied));
    }

    private static Procedure<ErrorCode, Void> checkPermission(CommentId commentId, Permission permission) {
        return componentFactory.commentGroup.commentInfo(commentId)
                .map(commentInfo -> commentInfo.commenter.equals(permission.userId) || permission.role.equals(Authenticator.Role.ADMIN))
                .filter(ok -> ok)
                .map(__ -> (Procedure<ErrorCode, Void>)Result.<ErrorCode, Void>of(null))
                .orElse(Fail.of(ErrorCode.PermissionDenied));
    }

    private static Procedure<ErrorCode, Void> checkPermission(ReplyId replyId, Permission permission) {
        return componentFactory.commentGroup.replyInfo(replyId)
                .map(replyInfo -> replyInfo.replier.equals(permission.userId) || permission.role.equals(Authenticator.Role.ADMIN))
                .filter(ok -> ok)
                .map(__ -> (Procedure<ErrorCode, Void>)Result.<ErrorCode, Void>of(null))
                .orElse(Fail.of(ErrorCode.PermissionDenied));
    }

    public static SchoolHeatInfo fetchSchoolHeatInfoAndUnwrap(SchoolHeatId id, Consumer<ErrorCode> nilCallBack) {
        return fetchSchoolHeatInfo(id).reduce(e -> {
            nilCallBack.accept(e);
            return null;
        }, i -> i);
    }

    public static Procedure<ErrorCode, SchoolHeatInfo> fetchSchoolHeatInfo(SchoolHeatId id) {
        return Procedure.fromOptional(componentFactory.schoolHeat.postInfo(id), ErrorCode.SchoolHeatNonExist);
    }

    private static Stream<SchoolHeatId> selectSchoolHeats(Commons.SortedBy sortedBy) {
        switch (sortedBy) {
            case ActiveTimeAsc: return componentFactory.active.getAllAsc(postGroupId, 8).map(SchoolHeatId::of);
            case ActiveTimeDes: return componentFactory.active.getAllDes(postGroupId, 8).map(SchoolHeatId::of);
            case HeatAsc: return componentFactory.heat.getAllAsc(postGroupId).map(SchoolHeatId::of);
            case HeatDes: return componentFactory.heat.getAllDes(postGroupId).map(SchoolHeatId::of);
            case NatureAsc: return componentFactory.schoolHeat.allPosts(false);
            case NatureDes: return componentFactory.schoolHeat.allPosts(true);
            default: return null;
        }
    }

    private static SchoolHeat.SchoolHeatInfo consSchoolHeatInfoRet(SchoolHeatId id, LazyVal<SchoolHeatInfo> info) {
        return new SchoolHeat.SchoolHeatInfo() {
            LazyVal<ActiveInfo> latestActiveInfo = LazyVal.of(() ->
                    AppHeatActive.fetchActiveInfoAndUnwrap(id));

            public String getId() { return id.value; }
            public String getTitle() { return info.get().title; }
            public Content getContent() { return fromIdToContent(ContentId.of(info.get().contentId)); }
            public PersonalInformation.PersonalInfo getAuthor() { return Commons.fetchPersonalInfoAndUnwrap(UserId.of(info.get().authorId), warnNil); }
            public PersonalInformation.PersonalInfo getLatestCommenter() {
                return nilOrTr(latestActiveInfo.get(), activeInfo ->
                        Commons.fetchPersonalInfoAndUnwrap(UserId.of(activeInfo.toucherId), warnNil));
            }
            public Long getLatestActiveTime() {
                return nilOrTr(latestActiveInfo.get(), activeInfo ->
                        activeInfo.time.toEpochMilli());
            }
            public Long getCreateTime() { return info.get().createTime.toEpochMilli(); }
            public Long getHeat() { return AppHeatActive.getHeat(id); }
            public Comment.AllComments getAllComments(Long skip, Long first) {
                return AppComment.consAllComments(CommentGroupId.of(info.get().commentGroupId), skip, first);
            }
        };
    }

    private static SchoolHeat.MultiSchoolHeats consMultiSchoolHeats(Supplier<Long> schoolHeatCount, Stream<SchoolHeat.SchoolHeatInfo> infos, long skip, long first) {
        return new SchoolHeat.MultiSchoolHeats() {
            public Long getTotalCount() {
                return schoolHeatCount.get();
            }
            public List<SchoolHeat.SchoolHeatInfo> getSchoolHeats() {
                return infos.skip(skip).limit(first).collect(Collectors.toList());
            }
        };
    }

    private static Procedure<ErrorCode, SchoolHeatId> publishSchoolHeat(SchoolHeatInfo schoolHeatInfo) {
        return Procedure.fromOptional(componentFactory.schoolHeat.publishPost(schoolHeatInfo),
                ErrorCode.CreatePostFailed
        ).then(id -> Result.of(id, () -> componentFactory.schoolHeat.removePost(id)));
    }

    private static SchoolHeat.SchoolHeatInfo consSchoolHeatInfoAfterCreate(SchoolHeatId id, SchoolHeat.SchoolHeatInput input,
                                                                           ContentId contentId, UserId authorId, SchoolHeatInfo info) {
        return new SchoolHeat.SchoolHeatInfo() {
            public String getId() { return id.value; }
            public String getTitle() { return input.title; }
            public Content getContent() { return fromIdToContent(contentId); }
            public PersonalInformation.PersonalInfo getAuthor() {
                return Commons.fetchPersonalInfoAndUnwrap(authorId, warnNil);
            }
            public PersonalInformation.PersonalInfo getLatestCommenter() {
                return null;
            }
            public Long getLatestActiveTime() { return info.createTime.toEpochMilli(); }
            public Long getCreateTime() { return info.createTime.toEpochMilli(); }
            public Long getHeat() { return 1L; }
            public Comment.AllComments getAllComments(Long skip, Long first) {
                return new Comment.AllComments() {
                    public Long getTotalCount() { return 0L; }
                    public List<Comment.CommentInfo> getComments() { return new LinkedList<>(); }
                };
            }
        };
    }

    private static <T, E> T warnNil(E error) {
        log.warn("null warning: {}", error);
        return null;
    }

    private static Content fromIdToContent(ContentId contentId) {
        return AppContent.fromContentId(contentId).reduce(AppSchoolHeat::warnNil, i -> i);
    }

    private static <T, R> R nilOrTr(T obj, Function<T, R> transformer) {
        if (obj == null) return null;
        else return transformer.apply(obj);
    }

    public static void reset() {
        componentFactory.schoolHeat.allPosts().forEach(id -> {
            fetchSchoolHeatInfo(id)
                    .then(info -> deleteSchoolHeatInfo(id, info))
                    .then(__ -> AppHeatActive.clearActiveAndHeat(id));
        });
    }
}
