package com.gaufoo.bbs.application;

import com.gaufoo.bbs.application.error.Error;
import com.gaufoo.bbs.application.error.ErrorCode;
import com.gaufoo.bbs.application.error.Ok;
import com.gaufoo.bbs.application.types.Comment;
import com.gaufoo.bbs.application.types.Content;
import com.gaufoo.bbs.application.types.LearningResource;
import com.gaufoo.bbs.application.types.PersonalInformation;
import com.gaufoo.bbs.application.util.LazyVal;
import com.gaufoo.bbs.application.util.StaticResourceConfig;
import com.gaufoo.bbs.components.active.common.ActiveInfo;
import com.gaufoo.bbs.components.authenticator.Authenticator;
import com.gaufoo.bbs.components.authenticator.common.Permission;
import com.gaufoo.bbs.components.authenticator.common.UserToken;
import com.gaufoo.bbs.components.commentGroup.comment.common.CommentId;
import com.gaufoo.bbs.components.commentGroup.comment.common.CommentInfo;
import com.gaufoo.bbs.components.commentGroup.comment.reply.common.ReplyId;
import com.gaufoo.bbs.components.commentGroup.comment.reply.common.ReplyInfo;
import com.gaufoo.bbs.components.commentGroup.common.CommentGroupId;
import com.gaufoo.bbs.components.content.common.ContentId;
import com.gaufoo.bbs.components.file.common.FileId;
import com.gaufoo.bbs.components.learningResource.common.LearningResourceId;
import com.gaufoo.bbs.components.learningResource.common.LearningResourceInfo;
import com.gaufoo.bbs.components.scutCourse.common.Course;
import com.gaufoo.bbs.components.scutCourse.common.CourseCode;
import com.gaufoo.bbs.components.user.common.UserId;
import com.gaufoo.bbs.util.Tuple;
import com.sun.istack.internal.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.gaufoo.bbs.application.ComponentFactory.componentFactory;
import static com.gaufoo.bbs.util.TaskChain.*;

public class AppLearningResource {
    private static final Logger log = LoggerFactory.getLogger(AppLearningResource.class);
    private static final Consumer<ErrorCode> warnNil = errorCode -> log.warn("null warning: {}", errorCode);
    private static final String postGroupId = Commons.getGroupId(Commons.PostType.LearningResource);

    public static LearningResource.AllLearningResourceResult allLearningResources(Long nullableSkip, Long nullableFirst, String courseStr, Commons.SortedBy nullableSortedBy) {
        long skip = nullableSkip == null ? 0L : nullableSkip;
        long first = nullableFirst == null ? Long.MAX_VALUE : nullableFirst;
        Commons.SortedBy sortedBy = nullableSortedBy == null ? Commons.SortedBy.ActiveTimeDes : nullableSortedBy;

        Predicate<Supplier<CourseCode>> courseFilter;
        Procedure<ErrorCode, CourseCode> initResult;
        Supplier<Long> resourcesCount;

        if (courseStr == null) {
            courseFilter = __ -> true;
            initResult = Result.of(null);
            resourcesCount = () -> componentFactory.learningResource.allPostsCount();
        } else {
            initResult = parseCourse(courseStr);
            CourseCode cc = initResult.retrieveResult().orElse(null);
            courseFilter = (courseCode -> courseCode.get().equals(cc));
            resourcesCount = () -> componentFactory.learningResource.allPostsCountOfCourse(cc.value);
        }

        return initResult
                .mapR(nullableCourseCode -> selectLearnResources(sortedBy, nullableCourseCode))
                .mapR(resourceIdStream -> resourceIdStream
                        .map(resId -> Tuple.of(resId, LazyVal.of(() -> fetchLearningResourceInfoAndUnwrap(resId, warnNil))))
                        .filter(idLearnInfOTuple -> courseFilter.test(() -> CourseCode.of(idLearnInfOTuple.right.get().courseCode)))
                        .map(tup -> consLearningResourceInfoRet(tup.left, tup.right)))
                .reduce(Error::of, infoStream -> consMultiLearnResources(resourcesCount, infoStream, skip, first));

    }

    public static LearningResource.LearningResourcesOfAuthorResult learningResourcesOfAuthor(String authorId, Long nullableSkip, Long nullableFirst) {
        long skip = nullableSkip == null ? 0L : nullableSkip;
        long first = nullableFirst == null ? Long.MAX_VALUE : nullableFirst;

        Supplier<Long> totalCount = () -> componentFactory.learningResource.allPostsCountOfAuthor(authorId);

        Stream<LearningResource.LearningResourceInfo> retInfos = componentFactory.learningResource.allPostsByAuhtor(authorId)
                .map(learningResourceId -> consLearningResourceInfoRet(learningResourceId,
                        LazyVal.of(() -> fetchLearningResourceInfoAndUnwrap(learningResourceId, warnNil))));

        return Commons.ensureUserExist(UserId.of(authorId))
                .reduce(Error::of, __ -> consMultiLearnResources(totalCount, retInfos, skip, first));
    }

    public static LearningResource.LearningResourceInfoResult learningResourceInfo(String learningResourceId) {
        LearningResourceId learnId = LearningResourceId.of(learningResourceId);

        return fetchLearningResourceInfo(learnId)
                .reduce(Error::of, info -> consLearningResourceInfoRet(learnId, LazyVal.with(info)));
    }

    public static LearningResource.CreateLearningResourceResult createLearningResource(LearningResource.LearningResourceInput learningResourceInput, String userToken) {
        class Ctx {
            UserId userId;                            Void put(UserId userId)            { this.userId = userId;           return null; }
            LearningResourceInfo contructedLearnInfo; Void put(LearningResourceInfo lri) { this.contructedLearnInfo = lri; return null; }
            LearningResourceId learningResourceId;    Void put(LearningResourceId lri)   { this.learningResourceId = lri;  return null; }
        }
        Ctx ctx = new Ctx();

        return Commons.fetchUserId(UserToken.of(userToken)).mapR(ctx::put)
                .then(__ -> consLearningResourceInfo(ctx.userId, learningResourceInput)).mapR(ctx::put)
                .then(__ -> publishLearningResource(ctx.contructedLearnInfo)).mapR(ctx::put)
                .then(__ -> AppHeatActive.createActiveAndHeat(ctx.learningResourceId, ctx.userId))
                .reduce(Error::of, __ -> consLearningResourceInfoAfterCreate(ctx.userId, ctx.learningResourceId, learningResourceInput, ctx.contructedLearnInfo));
    }

    public static LearningResource.DeleteLearningResourceResult deleteLearningResource(String learningResourceId, String userToken) {
        LearningResourceId learnId = LearningResourceId.of(learningResourceId);
        class Ctx {
            LearningResourceInfo learnInfo; Void put(LearningResourceInfo info) { this.learnInfo = info; return null; }
        }
        Ctx ctx = new Ctx();

        return Commons.fetchPermission(UserToken.of(userToken))
                .then(permission -> checkPermission(learnId, permission))
                .then(__ -> fetchLearningResourceInfo(learnId)).mapR(ctx::put)
                .then(__ -> deleteLearnResourceInfo(learnId, ctx.learnInfo))
                .then(__ -> AppHeatActive.clearActiveAndHeat(learnId))
                .reduce(Error::of, __ -> Ok.build());
    }

    public static LearningResource.CreateLearningResourceCommentResult createLearningResourceComment(LearningResource.LearningResourceCommentInput input, String userToken) {
        class Ctx {
            UserId userId;                  Void put(UserId userId)             { this.userId = userId;    return null; }
            LearningResourceInfo learnInfo; Void put(LearningResourceInfo info) { this.learnInfo = info;   return null; }
            ContentId contentId;            Void put(ContentId id)              { this.contentId = id;     return null; }
            CommentId commentId;
            CommentInfo commentInfo;        Void put(Tuple<CommentId, CommentInfo> tup) { this.commentId = tup.left; this.commentInfo = tup.right; return null; }
        }
        Ctx ctx = new Ctx();

        LearningResourceId learnId = LearningResourceId.of(input.postIdCommenting);
        return Commons.fetchUserId(UserToken.of(userToken)).mapR(ctx::put)
                .then(__ -> fetchLearningResourceInfo(learnId)).mapR(ctx::put)
                .then(__ -> AppContent.storeContentInput(input.content)).mapR(ctx::put)
                .then(__ -> AppComment.postComment(CommentGroupId.of(ctx.learnInfo.commentGroupId), ctx.contentId, ctx.userId)).mapR(ctx::put)
                .then(__ -> AppHeatActive.alterHeat(learnId, ctx.commentInfo.creationTime, 1L))
                .then(__ -> AppHeatActive.touchActive(learnId, UserId.of(ctx.commentInfo.commenter)))
                .reduce(Error::of, __ -> AppComment.consCommentInfoInstant(ctx.commentId, ctx.contentId, ctx.userId, ctx.commentInfo));
    }

    public static LearningResource.DeleteLearningResourceCommentResult deleteLearningResourceComment(String learningResourceId, String commentIdStr, String userToken) {
        LearningResourceId learnId = LearningResourceId.of(learningResourceId);
        CommentId commentId = CommentId.of(commentIdStr);

        class Ctx {
            LearningResourceInfo learningResourceInfo; Void put(LearningResourceInfo info) { this.learningResourceInfo = info; return null; }
        }
        Ctx ctx = new Ctx();

        return Commons.fetchPermission(UserToken.of(userToken))
                .then(permission -> checkPermission(commentId, permission))
                .then(__ -> fetchLearningResourceInfo(learnId)).mapR(ctx::put)
                .then(__ -> AppHeatActive.alterHeat(learnId, ctx.learningResourceInfo.createTime, -1))
                .then(__ -> AppComment.deleteComment(CommentGroupId.of(ctx.learningResourceInfo.commentGroupId), commentId))
                .reduce(Error::of, __ -> Ok.build());
    }

    public static LearningResource.CreateLearningResourceCommentReplyResult createLearningResourceCommentReply(LearningResource.LearningResourceReplyInput input, String userToken) {
        class Ctx {
            UserId replierId;                  Void put(UserId id)                 { this.replierId = id;      return null; }
            ContentId contentId;               Void put(ContentId id)              { this.contentId = id;      return null; }
            LearningResourceInfo resourceInfo; Void put(LearningResourceInfo info) { this.resourceInfo = info; return null; }
            ReplyId replyId;
            ReplyInfo replyInfo; Void put(Tuple<ReplyId, ReplyInfo> tup) { replyId = tup.left; replyInfo = tup.right; return null; }
        }
        Ctx ctx = new Ctx();

        LearningResourceId learnId = LearningResourceId.of(input.postIdReplying);
        CommentId postIdReplying = CommentId.of(input.commentIdReplying);
        UserId nullableReplyTo = nilOrTr(input.replyTo, UserId::of);

        return Commons.fetchUserId(UserToken.of(userToken)).mapR(ctx::put)
                .then(__ -> fetchLearningResourceInfo(learnId)).mapR(ctx::put)
                .then(__ -> nullableReplyTo == null ? Result.of(null) : Commons.ensureUserExist(nullableReplyTo))
                .then(__ -> AppContent.storeContentInput(input.content)).mapR(ctx::put)
                .then(__ -> AppComment.postReply(
                        CommentGroupId.of(ctx.resourceInfo.commentGroupId), postIdReplying, ctx.contentId, ctx.replierId, nullableReplyTo)).mapR(ctx::put)
                .then(__ -> AppHeatActive.alterHeat(learnId, ctx.replyInfo.creationTime, 1L))
                .then(__ -> AppHeatActive.touchActive(learnId, ctx.replierId))
                .reduce(Error::of, __ -> AppComment.consReplyInfoInstant(ctx.replyId, ctx.replyInfo, ctx.contentId, ctx.replierId));
    }

    public static LearningResource.DeleteLearningResourceCommentReplyResult deleteLearningResourceCommentReply(String learnResourceId, String commentIdStr, String replyIdStr, String userToken) {
        LearningResourceId learnId = LearningResourceId.of(learnResourceId);
        CommentId commentId = CommentId.of(commentIdStr);
        ReplyId replyId = ReplyId.of(replyIdStr);

        return Commons.fetchPermission(UserToken.of(userToken))
                .then(permission -> checkPermission(replyId, permission))
                .then(__ -> fetchLearningResourceInfo(learnId))
                .then(leInfo -> AppHeatActive.alterHeat(learnId, leInfo.createTime, -1L))
                .then(__ -> AppComment.deleteReply(commentId, replyId))
                .reduce(Error::of, __ -> Ok.build());
    }

    public static void reset() {
        componentFactory.learningResource.allPosts().forEach(resourceId -> {
            fetchLearningResourceInfo(resourceId)
                    .then(resourceInfo -> deleteLearnResourceInfo(resourceId, resourceInfo))
                    .then(__ -> AppHeatActive.clearActiveAndHeat(resourceId));
        });
    }

    private static Stream<LearningResourceId> selectLearnResources(Commons.SortedBy sortedBy, @Nullable CourseCode courseCode) {
        Function<Boolean, Stream<LearningResourceId>> natural = courseCode == null ?
                (isDes) -> componentFactory.learningResource.allPosts(isDes) :
                (isDes) -> componentFactory.learningResource.allPostsOfCourse(courseCode.value, isDes);

        Stream<LearningResourceId> dataSource;
        switch (sortedBy) {
            case ActiveTimeAsc: dataSource = componentFactory.active.getAllAsc(postGroupId).map(LearningResourceId::of); break;
            case ActiveTimeDes: dataSource = componentFactory.active.getAllDes(postGroupId).map(LearningResourceId::of); break;
            case HeatAsc: dataSource = componentFactory.heat.getAllAsc(postGroupId).map(LearningResourceId::of); break;
            case HeatDes: dataSource = componentFactory.heat.getAllDes(postGroupId).map(LearningResourceId::of); break;
            case NatureAsc: dataSource = natural.apply(false); break;
            case NatureDes: dataSource = natural.apply(true); break;
            default: dataSource = null; // impossible to reach
        }

        return dataSource;
    }

    private static Procedure<ErrorCode, LearningResourceId> publishLearningResource(LearningResourceInfo learningResourceInfo) {
        return Procedure.fromOptional(componentFactory.learningResource.publishPost(learningResourceInfo), ErrorCode.PublishLearningResourceFailed)
                .then(learningResourceId -> Result.of(learningResourceId, () -> componentFactory.learningResource.removePost(learningResourceId)));
    }

    private static Procedure<ErrorCode, LearningResourceInfo> consLearningResourceInfo(UserId userId, LearningResource.LearningResourceInput input) {
        class Ctx {
            CourseCode courseCode;         Void put(CourseCode courseCode)  { this.courseCode = courseCode; return null; }
            ContentId contentId;           Void put(ContentId   contentId)  { this.contentId = contentId;   return null; }
            FileId nullableFileId;         Void put(FileId         fileId)  { this.nullableFileId = fileId; return null; }
            CommentGroupId commentGroupId; Void put(CommentGroupId  cmgId)  { this.commentGroupId = cmgId;  return null; }
        }
        Ctx ctx = new Ctx();

        return parseCourse(input.course).mapR(ctx::put)
                .then(__ -> AppContent.storeContentInput(input.content)).mapR(ctx::put)
                .then(__ -> input.base64AttachedFile == null ?
                        Result.of(null) :
                        Commons.storeBase64File(componentFactory.learningResourceAttachFiles, input.base64AttachedFile)).mapR(ctx::put)
                .then(__ -> AppComment.createCommentGroup()).mapR(ctx::put)
                .then(__ -> Result.of(LearningResourceInfo.of(userId.value, input.title, ctx.contentId.value,
                        ctx.courseCode.value, nilOrTr(ctx.nullableFileId, x -> x.value), ctx.commentGroupId.value)));
    }

    private static Procedure<ErrorCode, Void> deleteLearnResourceInfo(LearningResourceId id, LearningResourceInfo info) {
        if (info.attachedFileId != null) {
            componentFactory.learningResourceAttachFiles.Remove(FileId.of(info.attachedFileId));
        }
        return AppComment.deleteAllComments(CommentGroupId.of(info.commentGroupId))
                .then(__ -> AppContent.deleteContent(ContentId.of(info.contentId)))
                .mapR(__ -> componentFactory.learningResource.removePost(id))
                .then(ok -> ok ? Result.of(null) : Fail.of(ErrorCode.DeleteLearningResourceFailed));
    }

    private static LearningResource.MultiLearningResources consMultiLearnResources(Supplier<Long> resourcesCount,
                                                                                   Stream<LearningResource.LearningResourceInfo> infos,
                                                                                   long skip, long first) {
        return new LearningResource.MultiLearningResources() {
            public Long getTotalCount() { return resourcesCount.get(); }
            public List<LearningResource.LearningResourceInfo> getLearningResources() {
                return infos.skip(skip).limit(first).collect(Collectors.toList());
            }
        };
    }

    private static LearningResource.LearningResourceInfo consLearningResourceInfoRet(LearningResourceId resourceId, LazyVal<LearningResourceInfo> info) {
        return new LearningResource.LearningResourceInfo() {
            LazyVal<ActiveInfo> latestActiveInfo = LazyVal.of(() ->
                    componentFactory.active.getLatestActiveInfo(postGroupId, resourceId.value)
                            .orElseGet(() -> warnNil(ErrorCode.LatestActiveNotFound)));

            public String getId() { return resourceId.value; }
            public PersonalInformation.PersonalInfo getAuthor() {
                return Commons.fetchPersonalInfoAndUnwrap(UserId.of(info.get().authorId), warnNil);
            }
            public String getTitle()    { return info.get().title; }
            public Long getCreateTime() { return info.get().createTime.toEpochMilli(); }
            public Content getContent() {
                return fromIdToContent(ContentId.of(info.get().contentId));
            }
            public String getCourse() {
                return componentFactory.course.getCourseFromCode(CourseCode.of(info.get().courseCode)).map(Enum::toString).orElse(null);
            }
            public String getAttachedFileURL() {
                if (info.get().attachedFileId == null) return null;
                return Commons.fetchFileUrlAndUnwrap(componentFactory.learningResourceAttachFiles,
                        StaticResourceConfig.FileType.AttachFiles, FileId.of(info.get().attachedFileId), warnNil);
            }
            public PersonalInformation.PersonalInfo getLatestCommenter() {
                return nilOrTr(latestActiveInfo.get(), activeInfo ->
                    Commons.fetchPersonalInfoAndUnwrap(UserId.of(activeInfo.toucherId), (e) -> {})
                );
            }
            public Long getLatestActiveTime() {
                return nilOrTr(latestActiveInfo.get(), x -> x.time.toEpochMilli());
            }
            public Comment.AllComments getAllComments(Long skip, Long first) {
                skip = skip == null ? 0L : skip;
                first = first == null ? Long.MAX_VALUE : first;
                return AppComment.consAllComments(CommentGroupId.of(info.get().commentGroupId), skip, first);
            }
        };
    }

    private static LearningResource.LearningResourceInfo consLearningResourceInfoAfterCreate(UserId userId, LearningResourceId resourceId,
                                                                                             LearningResource.LearningResourceInput input,
                                                                                             LearningResourceInfo commonInfo) {
        return new LearningResource.LearningResourceInfo() {
            public String getId()               { return resourceId.value; }
            public String getTitle()            { return input.title; }
            public Content getContent()         { return fromIdToContent(ContentId.of(commonInfo.contentId)); }
            public String getCourse()           { return input.course; }
            public Long getLatestActiveTime()   { return Instant.now().toEpochMilli(); }
            public Long getCreateTime()         { return Instant.now().toEpochMilli(); }
            public PersonalInformation.PersonalInfo getLatestCommenter() { return null; }
            public PersonalInformation.PersonalInfo getAuthor() {
                return Commons.fetchPersonalInfoAndUnwrap(userId, warnNil);
            }
            public String getAttachedFileURL() {
                if (commonInfo.attachedFileId == null) return null;
                return Commons.fetchFileUrlAndUnwrap(componentFactory.learningResourceAttachFiles, StaticResourceConfig.FileType.AttachFiles,
                        FileId.of(commonInfo.attachedFileId), warnNil);
            }
            public Comment.AllComments getAllComments(Long skip, Long first) {
                return new Comment.AllComments() {
                    public Long getTotalCount() { return 0L; }
                    public List<Comment.CommentInfo> getComments() { return new LinkedList<>(); }
                };
            }
        };
    }

    private static Procedure<ErrorCode, Void> checkPermission(LearningResourceId learningResourceId, Permission permission) {
        return fetchLearningResourceInfo(learningResourceId)
                .mapR(learningResourceInfo -> learningResourceInfo.authorId.equals(permission.userId) || permission.role.equals(Authenticator.Role.ADMIN))
                .then(ok -> ok ? Result.of(null) : Fail.of(ErrorCode.PermissionDenied));
    }

    private static Procedure<ErrorCode, Void> checkPermission(CommentId commentId, Permission permission) {
        return AppComment.fetchCommentInfo(commentId)
                .mapR(commentInfo -> commentInfo.commenter.equals(permission.userId) || permission.role.equals(Authenticator.Role.ADMIN))
                .then(ok -> ok ? Result.of(null) : Fail.of(ErrorCode.PermissionDenied));
    }

    private static Procedure<ErrorCode,Void> checkPermission(ReplyId replyId, Permission permission) {
        return AppComment.fetchReplyInfo(replyId)
                .mapR(replyInfo -> replyInfo.replier.equals(permission.userId) || permission.role.equals(Authenticator.Role.ADMIN))
                .then(ok -> ok ? Result.of(null) : Fail.of(ErrorCode.PermissionDenied));
    }

    private static LearningResourceInfo fetchLearningResourceInfoAndUnwrap(LearningResourceId id, Consumer<ErrorCode> nilCallback) {
        return fetchLearningResourceInfo(id).reduce(e -> {
            nilCallback.accept(e);
            return null;
        }, i -> i);
    }

    private static Procedure<ErrorCode, LearningResourceInfo> fetchLearningResourceInfo(LearningResourceId id) {
        return Procedure.fromOptional(componentFactory.learningResource.postInfo(id), ErrorCode.LearningResourceNonExist);
    }

    private static Procedure<ErrorCode, CourseCode> parseCourse(String courseStr) {
        return Procedure.fromOptional(Arrays.stream(Course.values())
                .filter(course -> course.toString().equals(courseStr))
                .findFirst(), ErrorCode.ParseCourseError
        ).then(course -> Result.of(componentFactory.course.generateCourseCode(course)));
    }

    private static Content fromIdToContent(ContentId contentId) {
        return AppContent.fromContentId(contentId).reduce(AppLearningResource::warnNil, i -> i);
    }

    private static <T, E> T warnNil(E error) {
        log.warn("null warning: {}", error);
        return null;
    }

    private static <T, R> R nilOrTr(T obj, Function<T, R> transformer) {
        if (obj == null) return null;
        else return transformer.apply(obj);
    }
}
