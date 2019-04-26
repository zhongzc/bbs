package com.gaufoo.bbs.application;

import com.gaufoo.bbs.application.error.Error;
import com.gaufoo.bbs.application.error.ErrorCode;
import com.gaufoo.bbs.application.types.Comment;
import com.gaufoo.bbs.application.types.Content;
import com.gaufoo.bbs.application.types.PersonalInformation;
import com.gaufoo.bbs.application.util.LazyVal;
import com.gaufoo.bbs.components.active.Active;
import com.gaufoo.bbs.components.active.common.ActiveInfo;
import com.gaufoo.bbs.components.authenticator.common.UserToken;
import com.gaufoo.bbs.components.commentGroup.CommentGroup;
import com.gaufoo.bbs.components.commentGroup.common.CommentGroupId;
import com.gaufoo.bbs.components.content.common.ContentId;
import com.gaufoo.bbs.components.content.common.ContentInfo;
import com.gaufoo.bbs.components.heat.Heat;
import com.gaufoo.bbs.components.schoolHeat.common.SchoolHeatId;
import com.gaufoo.bbs.components.schoolHeat.common.SchoolHeatInfo;

import static com.gaufoo.bbs.util.TaskChain.Procedure.*;
import static com.gaufoo.bbs.util.TaskChain.*;

import com.gaufoo.bbs.components.user.common.UserId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.gaufoo.bbs.application.ComponentFactory.componentFactory;

import com.gaufoo.bbs.application.types.SchoolHeat;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AppSchoolHeat {
    public static Logger log = LoggerFactory.getLogger(SchoolHeat.class);

    public static SchoolHeat.AllSchoolHeatsResult allSchoolHeats(Long skip, Long first, Commons.SortedBy sortedBy) {
        final long fSkip = skip == null ? 0L : skip;
        final long fFirst = first == null ? Long.MAX_VALUE : first;
        final Commons.SortedBy fSb = sortedBy == null ? Commons.SortedBy.ActiveTimeDes : sortedBy;

        return new SchoolHeat.MultiSchoolHeats() {
            public Long getTotalCount() { return componentFactory.schoolHeat.allPostsCount(); }
            public List<SchoolHeat.SchoolHeatInfo> getSchoolHeats() {
                Stream<SchoolHeatId> ids = null;
                String g = Commons.getGroupId(Commons.PostType.SchoolHeat);
                switch (fSb) {
                    case ActiveTimeAsc: ids = componentFactory.active.getAllAsc(g).map(SchoolHeatId::of); break;
                    case ActiveTimeDes: ids = componentFactory.active.getAllDes(g).map(SchoolHeatId::of); break;
                    case HeatAsc:       ids = componentFactory.heat.getAllAsc(g).map(SchoolHeatId::of);   break;
                    case HeatDes:       ids = componentFactory.heat.getAllDes(g).map(SchoolHeatId::of);   break;
                    case NatureAsc:     ids = componentFactory.schoolHeat.allPosts(false);                break;
                    case NatureDes:     ids = componentFactory.schoolHeat.allPosts(true);                 break;
                }
                return ids.map(id -> consSH(id, LazyVal.of(() -> componentFactory.schoolHeat.postInfo(id).orElse(null))))
                        .skip(fSkip).limit(fFirst).collect(Collectors.toList());
            }
        };
    }

    public static SchoolHeat.CreateSchoolHeatResult createSchoolHeat(SchoolHeat.SchoolHeatInput input, String loginToken) {
        CommentGroup commentGroup = componentFactory.commentGroup;
        Heat heat = componentFactory.heat;
        Active active = componentFactory.active;
        ErrorCode e = ErrorCode.CreatePostFailed;
        String schoolHeatGroup = Commons.getGroupId(Commons.PostType.SchoolHeat);
        String aTimeWindow = Commons.currentActiveTimeWindow(Instant.now());
        String hTimeWindow = Commons.currentHeatTimeWindow(Instant.now());

        return Commons.fetchUserId(UserToken.of(loginToken))
        /* 构造评论句柄     */ .then(userId -> fromOptional(commentGroup.cons(), e)
        /* 构造内容信息     */ .then(cgId   -> AppContent.consContent(input.content)
        /* 构造内容句柄     */ .then(ctInfo -> fromOptional(componentFactory.content.cons(ctInfo),                                  e, () -> commentGroup.removeComments(cgId))
        /* 构造帖子        */ .then(ctId   -> Result.of(SchoolHeatInfo.of(input.title, ctId.value, userId.value, cgId.value),         () -> componentFactory.content.remove(ctId)))
        /* 发表帖子        */ .then(ptInfo -> fromOptional(componentFactory.schoolHeat.publishPost(ptInfo),                        e)
        /* 构造热度        */ .then(ptId   -> fromOptional(heat.increase(schoolHeatGroup, ptId.value, 1),                          e, () -> componentFactory.schoolHeat.removePost(ptId))
        /* 构造最热        */ .then(ht     -> fromOptional(heat.increase(hTimeWindow, schoolHeatGroup + ptId.value, 1),            e, () -> heat.remove(schoolHeatGroup, ptId.value))
        /* 构造活跃        */ .then(__     -> fromOptional(active.touch(schoolHeatGroup, ptId.value, null),                e, () -> heat.remove(hTimeWindow, schoolHeatGroup + ptId.value)))
        /* 构造最新        */ .then(at     -> fromOptional(active.touch(aTimeWindow, schoolHeatGroup + ptId.value, userId.value),  e, () -> active.remove(schoolHeatGroup, ptId.value))
        /* 构造最终返回值   */ .then(__     -> Result.of(consSH(ptInfo, ptId, ht, at, ctInfo, cgId),                                   () -> active.remove(aTimeWindow, schoolHeatGroup + ptId.value))))))))))
                            .reduce(Error::of, i -> i);
    }

    private static SchoolHeat.SchoolHeatInfo consSH(SchoolHeatInfo info, SchoolHeatId id, Long heat, ActiveInfo activeInfo, ContentInfo contentInfo, CommentGroupId commentGroupId) {
        return new SchoolHeat.SchoolHeatInfo() {
            public String                                            getId() { return id.value; }
            public Long                                            getHeat() { return heat; }
            public String                                         getTitle() { return info.title; }
            public Content                                      getContent() { return AppContent.fromContentInfo(contentInfo); }
            public Long                                      getCreateTime() { return info.createTime.toEpochMilli(); }
            public Long                                getLatestActiveTime() { return activeInfo.time.toEpochMilli(); }
            public PersonalInformation.PersonalInfo              getAuthor() { return Commons.fetchPersonalInfo(UserId.of(info.authorId)).reduce(i -> null, i -> i); }
            public PersonalInformation.PersonalInfo     getLatestCommenter() { return nilOrTr(activeInfo.toucherId, c -> Commons.fetchPersonalInfo(UserId.of(c)).reduce(i -> null, i -> i)); }
            public Comment.AllComments getAllComments(Long skip, Long first) { return AppComment.consAllComments(commentGroupId, skip, first); }
        };
    }

//    private static SchoolHeat.SchoolHeatInfo consSH(SchoolHeatId schoolHeatId) {
//        String g = Commons.getGroupId(Commons.PostType.SchoolHeat);
//        return componentFactory.schoolHeat.postInfo(schoolHeatId)                          .flatMap(
//                info -> componentFactory.heat.getHeat(g, schoolHeatId.value)               .flatMap(
//                ht -> componentFactory.active.getLatestActiveInfo(g, schoolHeatId.value)   .flatMap(
//                aInfo -> componentFactory.content.contentInfo(ContentId.of(info.contentId)).map(
//                ctInfo -> consSH(info, schoolHeatId, ht, aInfo, ctInfo, CommentGroupId.of(info.commentGroupId)))))).orElse(null);
//    }

    private static SchoolHeat.SchoolHeatInfo consSH(SchoolHeatId schoolHeatId, LazyVal<SchoolHeatInfo> schoolHeatInfo) {
        log.debug("{}: {}", schoolHeatId, schoolHeatInfo.get());
        return new SchoolHeat.SchoolHeatInfo() {
            private LazyVal<Optional<ActiveInfo>> la = LazyVal.of(() -> componentFactory.active
                    .getLatestActiveInfo(Commons.getGroupId(Commons.PostType.SchoolHeat), schoolHeatId.value));
            public String getId() {
                return schoolHeatId.value;
            }
            public String getTitle() {
                return schoolHeatInfo.get().title;
            }
            public Content getContent() {
                return AppContent.fromContentId(ContentId.of(schoolHeatInfo.get().contentId)).reduce(i -> null, i -> i);
            }
            public PersonalInformation.PersonalInfo getAuthor() {
                return Commons.fetchPersonalInfo(UserId.of(schoolHeatInfo.get().authorId)).reduce(i -> null, i -> i);
            }
            public PersonalInformation.PersonalInfo getLatestCommenter() {
                return la.get().map(aInfo -> nilOrTr(aInfo.toucherId, c -> Commons.fetchPersonalInfo(UserId.of(c)).reduce(i -> null, i -> i))).orElse(null);
            }
            public Long getLatestActiveTime() {
                return la.get().map(aInfo -> aInfo.time.toEpochMilli()).orElse(null);
            }
            public Long getCreateTime() {
                return schoolHeatInfo.get().createTime.toEpochMilli();
            }
            public Long getHeat() {
                return componentFactory.heat.getHeat(Commons.getGroupId(Commons.PostType.SchoolHeat), schoolHeatId.value).orElse(null);
            }
            public Comment.AllComments getAllComments(Long skip, Long first) {
                return AppComment.consAllComments(CommentGroupId.of(schoolHeatInfo.get().commentGroupId), skip, first);
            }
        };
    }

    private static <T, R> R nilOrTr(T obj, Function<T, R> transformer) {
        if (obj == null) return null;
        else return transformer.apply(obj);
    }
}
