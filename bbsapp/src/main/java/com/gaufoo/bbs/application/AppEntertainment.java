package com.gaufoo.bbs.application;

import com.gaufoo.bbs.application.error.Error;
import com.gaufoo.bbs.application.error.ErrorCode;
import com.gaufoo.bbs.application.types.Comment;
import com.gaufoo.bbs.application.types.Content;
import com.gaufoo.bbs.application.types.Entertainment;
import com.gaufoo.bbs.application.types.PersonalInformation;
import com.gaufoo.bbs.components.authenticator.common.UserToken;
import com.gaufoo.bbs.components.commentGroup.common.CommentGroupId;
import com.gaufoo.bbs.components.content.common.ContentId;
import com.gaufoo.bbs.components.entertainment.common.EntertainmentId;
import com.gaufoo.bbs.components.entertainment.common.EntertainmentInfo;
import com.gaufoo.bbs.components.user.common.UserId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import static com.gaufoo.bbs.application.ComponentFactory.componentFactory;
import static com.gaufoo.bbs.util.TaskChain.Procedure;
import static com.gaufoo.bbs.util.TaskChain.Result;

public class AppEntertainment {
    private static Logger log = LoggerFactory.getLogger(AppEntertainment.class);
    private static Consumer<ErrorCode> warnNil = errorCode -> log.warn("null warning: {}", errorCode);
    private static final String postGroupId = Commons.getGroupId(Commons.PostType.Entertainment);


    public static Entertainment.CreateEntertainmentResult createEntertainment(Entertainment.EntertainmentInput input, String userToken) {
        class Ctx {
            UserId userId;                   Void put(UserId id)          { this.userId = id;          return null; }
            ContentId contentId;             Void put(ContentId id)       { this.contentId = id;       return null; }
            CommentGroupId commentGroupId;   Void put(CommentGroupId id)  { this.commentGroupId = id;  return null; }
            EntertainmentId entertainmentId; Void put(EntertainmentId id) { this.entertainmentId = id; return null; }
        } Ctx ctx = new Ctx();

        return Commons.fetchUserId(UserToken.of(userToken)).mapR(ctx::put)
                .then(__ -> AppContent.storeContentInput(input.content)).mapR(ctx::put)
                .then(__ -> AppComment.createCommentGroup()).mapR(ctx::put)
                .then(__ -> publishEntertainment(EntertainmentInfo.of(input.title, ctx.contentId.value, ctx.userId.value, ctx.commentGroupId.value))).mapR(ctx::put)
                .then(__ -> AppHeatActive.createActiveAndHeat(ctx.entertainmentId, ctx.userId))
                .reduce(Error::of, __ -> consEntertainmentInfoAfterCreate(ctx.entertainmentId, input, ctx.contentId, ctx.userId));
    }

    private static Procedure<ErrorCode, EntertainmentId> publishEntertainment(EntertainmentInfo entertainmentInfo) {
        return Procedure.fromOptional(componentFactory.entertainment.publishPost(entertainmentInfo),
                ErrorCode.PublishEntertainmentFailed
        ).then(entertainmentId -> Result.of(entertainmentId, () -> componentFactory.entertainment.removePost(entertainmentId)));
    }

    private static Entertainment.EntertainmentInfo consEntertainmentInfoAfterCreate(EntertainmentId entertainmentId, Entertainment.EntertainmentInput input,
                                                                                    ContentId contentId, UserId authorId) {
        return new Entertainment.EntertainmentInfo() {
            public String getId()             { return entertainmentId.value;        }
            public String getTitle()          { return input.title;                  }
            public Content getContent()       { return fromIdToContent(contentId);   }
            public Long getLatestActiveTime() { return Instant.now().toEpochMilli(); }
            public Long getCreateTime()       { return Instant.now().toEpochMilli(); }
            public Integer getHeat()          { return 1;                            }
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

    private static <T, E> T warnNil(E error) {
        log.warn("null warning: {}", error);
        return null;
    }

    private static Content fromIdToContent(ContentId contentId) {
        return AppContent.fromContentId(contentId).reduce(AppEntertainment::warnNil, i -> i);
    }
}
