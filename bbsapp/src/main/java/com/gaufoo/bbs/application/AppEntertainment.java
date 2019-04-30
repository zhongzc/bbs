package com.gaufoo.bbs.application;

import com.gaufoo.bbs.application.error.Error;
import com.gaufoo.bbs.application.error.ErrorCode;
import com.gaufoo.bbs.application.error.Ok;
import com.gaufoo.bbs.application.types.Comment;
import com.gaufoo.bbs.application.types.Content;
import com.gaufoo.bbs.application.types.Entertainment;
import com.gaufoo.bbs.application.types.PersonalInformation;
import com.gaufoo.bbs.application.util.LazyVal;
import com.gaufoo.bbs.components.active.common.ActiveInfo;
import com.gaufoo.bbs.components.authenticator.Authenticator;
import com.gaufoo.bbs.components.authenticator.common.Permission;
import com.gaufoo.bbs.components.authenticator.common.UserToken;
import com.gaufoo.bbs.components.commentGroup.comment.common.CommentId;
import com.gaufoo.bbs.components.commentGroup.common.CommentGroupId;
import com.gaufoo.bbs.components.content.common.ContentId;
import com.gaufoo.bbs.components.entertainment.common.EntertainmentId;
import com.gaufoo.bbs.components.entertainment.common.EntertainmentInfo;
import com.gaufoo.bbs.components.user.common.UserId;
import com.gaufoo.bbs.util.TaskChain;
import com.gaufoo.bbs.util.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.gaufoo.bbs.application.ComponentFactory.componentFactory;
import static com.gaufoo.bbs.util.TaskChain.*;

public class AppEntertainment {
    private static final Logger log = LoggerFactory.getLogger(AppEntertainment.class);
    private static final Consumer<ErrorCode> warnNil = errorCode -> log.warn("null warning: {}", errorCode);
    private static final String postGroupId = Commons.getGroupId(Commons.PostType.Entertainment);

    public static Entertainment.AllEntertainmentsResult allEntertainments(Long nullableSkip, Long nullableFirst, Commons.SortedBy nullableSortedBy) {
        long skip = nullableSkip == null ? 0L : nullableSkip;
        long first = nullableFirst == null ? Long.MAX_VALUE : nullableFirst;
        Commons.SortedBy sortedBy = nullableSortedBy == null ? Commons.SortedBy.ActiveTimeDes : nullableSortedBy;

        Supplier<Long> totalCount = () -> componentFactory.entertainment.allPostsCount();

        Stream<Entertainment.EntertainmentInfo> infoStream = selectEntertainments(sortedBy)
                .map(entertainmentId -> Tuple.of(entertainmentId, LazyVal.of(() -> fetchEntertainmentInfoAndUnwrap(entertainmentId, warnNil))))
                .map(idInfoTup -> consEntertainmentInfoRet(idInfoTup.left, idInfoTup.right));

        return consMultiEntertainments(totalCount, infoStream, skip, first);
    }

    public static Entertainment.EntertainmentInfoResult entertainmentInfo(String entertainmentId) {
        EntertainmentId entertainId = EntertainmentId.of(entertainmentId);

        return fetchEntertainmentInfo(entertainId)
                .reduce(Error::of, info -> consEntertainmentInfoRet(entertainId, LazyVal.with(info)));
    }

    public static Entertainment.CreateEntertainmentResult createEntertainment(Entertainment.EntertainmentInput input, String userToken) {
        class Ctx {
            UserId userId;                   Void put(UserId id)           { this.userId = id;          return null; }
            ContentId contentId;             Void put(ContentId id)        { this.contentId = id;       return null; }
            CommentGroupId commentGroupId;   Void put(CommentGroupId id)   { this.commentGroupId = id;  return null; }
            EntertainmentId entertainmentId; Void put(EntertainmentId id)  { this.entertainmentId = id; return null; }
            EntertainmentInfo entertainInfo; Void put(EntertainmentInfo i) { this.entertainInfo = i;    return null; }
        } Ctx ctx = new Ctx();

        return Commons.fetchUserId(UserToken.of(userToken)).mapR(ctx::put)
                .then(__ -> AppContent.storeContentInput(input.content)).mapR(ctx::put)
                .then(__ -> AppComment.createCommentGroup()).mapR(ctx::put)
                .mapR(__ -> EntertainmentInfo.of(input.title, ctx.contentId.value, ctx.userId.value, ctx.commentGroupId.value)).mapR(ctx::put)
                .then(__ -> publishEntertainment(ctx.entertainInfo)).mapR(ctx::put)
                .then(__ -> AppHeatActive.createActiveAndHeat(ctx.entertainmentId, ctx.userId))
                .reduce(Error::of, __ -> consEntertainmentInfoAfterCreate(ctx.entertainmentId, input, ctx.contentId, ctx.userId, ctx.entertainInfo));
    }

    public static Entertainment.DeleteEntertainmentResult deleteEntertainment(String entertainmentId, String userToken) {
        EntertainmentId entertainId = EntertainmentId.of(entertainmentId);
        class Ctx {
            EntertainmentInfo entertainmentInfo; Void put(EntertainmentInfo i) { entertainmentInfo = i; return null; }
        } Ctx ctx = new Ctx();

        return Commons.fetchPermission(UserToken.of(userToken))
                .then(permission -> checkPermission(entertainId, permission)).mapR(ctx::put)
                .then(__ -> deleteEntertainmentInfo(entertainId, ctx.entertainmentInfo))
                .reduce(Error::of, __ -> Ok.build());
    }

    public static void reset() {
        componentFactory.entertainment.allPosts().forEach(entertainmentId -> {
            fetchEntertainmentInfo(entertainmentId)
                    .then(info -> deleteEntertainmentInfo(entertainmentId, info))
                    .then(__ -> AppHeatActive.clearActiveAndHeat(entertainmentId));
        });
    }

    private static EntertainmentInfo fetchEntertainmentInfoAndUnwrap(EntertainmentId entertainmentId, Consumer<ErrorCode> nilCallback) {
        return fetchEntertainmentInfo(entertainmentId).reduce(e -> {
            nilCallback.accept(e);
            return null;
        }, i -> i);
    }

    private static Procedure<ErrorCode, EntertainmentInfo> fetchEntertainmentInfo(EntertainmentId entertainmentId) {
        return Procedure.fromOptional(componentFactory.entertainment.postInfo(entertainmentId), ErrorCode.EntertainmentNotFound);
    }

    private static Stream<EntertainmentId> selectEntertainments(Commons.SortedBy sortedBy) {
        switch (sortedBy) {
            case ActiveTimeAsc: return componentFactory.active.getAllAsc(postGroupId).map(EntertainmentId::of);
            case ActiveTimeDes: return componentFactory.active.getAllDes(postGroupId).map(EntertainmentId::of);
            case HeatAsc: return componentFactory.heat.getAllAsc(postGroupId).map(EntertainmentId::of);
            case HeatDes: return componentFactory.heat.getAllDes(postGroupId).map(EntertainmentId::of);
            case NatureAsc: return componentFactory.entertainment.allPosts(false);
            case NatureDes: return componentFactory.entertainment.allPosts(true);
            default: return null;
        }
    }

    private static Entertainment.MultiEntertainments consMultiEntertainments(Supplier<Long> entertainCount, Stream<Entertainment.EntertainmentInfo> infos, long skip, long first) {
        return new Entertainment.MultiEntertainments() {
            public Long getTotalCount() {
                return entertainCount.get();
            }
            public List<Entertainment.EntertainmentInfo> getEntertainments() {
                return infos.skip(skip).limit(first).collect(Collectors.toList());
            }
        };
    }

    static Entertainment.EntertainmentInfo consEntertainmentInfoRet(EntertainmentId entertainmentId, LazyVal<EntertainmentInfo> info) {
        return new Entertainment.EntertainmentInfo() {
            LazyVal<ActiveInfo> latestActiveInfo = LazyVal.of(() ->
                    AppHeatActive.fetchActiveInfoAndUnwrap(entertainmentId));

            public String getId()       { return entertainmentId.value;                               }
            public String getTitle()    { return info.get().title;                                    }
            public Content getContent() { return fromIdToContent(ContentId.of(info.get().contentId)); }
            public Long getCreateTime() { return info.get().createTime.toEpochMilli();                }
            public Long getHeat()    { return AppHeatActive.getHeat(entertainmentId);                 }
            public PersonalInformation.PersonalInfo getAuthor() {
                return Commons.fetchPersonalInfoAndUnwrap(UserId.of(info.get().authorId), warnNil);
            }
            public PersonalInformation.PersonalInfo getLatestCommenter() {
                return nilOrTr(latestActiveInfo.get(), activeInfo ->
                        Commons.fetchPersonalInfoAndUnwrap(UserId.of(activeInfo.toucherId), warnNil));
            }
            public Long getLatestActiveTime() {
                return nilOrTr(latestActiveInfo.get(), activeInfo ->
                        activeInfo.time.toEpochMilli());
            }
            public Comment.AllComments getAllComments(Long skip, Long first) {
                skip = skip == null ? 0L : skip;
                first = first == null ? Long.MAX_VALUE : first;
                return AppComment.consAllComments(CommentGroupId.of(info.get().commentGroupId), skip, first);
            }
        };
    }

    private static Procedure<ErrorCode, EntertainmentId> publishEntertainment(EntertainmentInfo entertainmentInfo) {
        return Procedure.fromOptional(componentFactory.entertainment.publishPost(entertainmentInfo),
                ErrorCode.PublishEntertainmentFailed
        ).then(entertainmentId -> Result.of(entertainmentId, () -> componentFactory.entertainment.removePost(entertainmentId)));
    }

    private static Entertainment.EntertainmentInfo consEntertainmentInfoAfterCreate(EntertainmentId entertainmentId, Entertainment.EntertainmentInput input,
                                                                                    ContentId contentId, UserId authorId, EntertainmentInfo info) {
        return new Entertainment.EntertainmentInfo() {
            public String getId()             { return entertainmentId.value;           }
            public String getTitle()          { return input.title;                     }
            public Content getContent()       { return fromIdToContent(contentId);      }
            public Long getLatestActiveTime() { return info.createTime.toEpochMilli();  }
            public Long getCreateTime()       { return info.createTime.toEpochMilli();  }
            public Long getHeat()             { return 1L;                              }
            public PersonalInformation.PersonalInfo getLatestCommenter() { return null; }
            public PersonalInformation.PersonalInfo getAuthor() {
                return Commons.fetchPersonalInfoAndUnwrap(authorId, warnNil);
            }
            public Comment.AllComments getAllComments(Long skip, Long first) {
                return new Comment.AllComments() {
                    public Long getTotalCount()                    { return 0L;                 }
                    public List<Comment.CommentInfo> getComments() { return new LinkedList<>(); }
                };
            }
        };
    }

    private static Procedure<ErrorCode, EntertainmentInfo> checkPermission(EntertainmentId id, Permission permission) {
        class Ctx {
            EntertainmentInfo entertainInfo; Void put(EntertainmentInfo info) { this.entertainInfo = info; return null; }
        } Ctx ctx = new Ctx();

        return Procedure.fromOptional(componentFactory.entertainment.postInfo(id), ErrorCode.EntertainmentNotFound).mapR(ctx::put)
                .mapR(__ -> ctx.entertainInfo.authorId.equals(permission.userId) || permission.role.equals(Authenticator.Role.ADMIN))
                .then(ok -> ok ? Result.of(ctx.entertainInfo) : Fail.of(ErrorCode.PermissionDenied));
    }

    private static Procedure<ErrorCode, Void> deleteEntertainmentInfo(EntertainmentId entertainmentId, EntertainmentInfo info) {
        return AppContent.deleteContent(ContentId.of(info.contentId))
                .then(__ -> AppComment.deleteAllComments(CommentGroupId.of(info.commentGroupId)))
                .mapR(__ -> componentFactory.entertainment.removePost(entertainmentId))
                .then(ok -> ok ? Result.of(null) : Fail.of(ErrorCode.DeleteEntertainmentFailed));
    }

    private static <T, E> T warnNil(E error) {
        log.warn("null warning: {}", error);
        return null;
    }

    private static Content fromIdToContent(ContentId contentId) {
        return AppContent.fromContentId(contentId).reduce(AppEntertainment::warnNil, i -> i);
    }

    private static <T, R> R nilOrTr(T obj, Function<T, R> transformer) {
        if (obj == null) return null;
        else return transformer.apply(obj);
    }
}
