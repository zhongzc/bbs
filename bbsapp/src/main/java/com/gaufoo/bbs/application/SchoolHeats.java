package com.gaufoo.bbs.application;

import com.gaufoo.bbs.application.util.Utils;
import com.gaufoo.bbs.components.authenticator.common.UserToken;
import com.gaufoo.bbs.components.authenticator.exceptions.AuthenticatorException;
import com.gaufoo.bbs.components.reply.common.ReplyId;
import com.gaufoo.bbs.components.reply.common.ReplyInfo;
import com.gaufoo.bbs.components.schoolHeat.common.PostComparators;
import com.gaufoo.bbs.components.schoolHeat.common.PostId;
import com.gaufoo.bbs.components.schoolHeat.common.PostInfo;
import com.gaufoo.bbs.components.user.common.UserId;
import com.gaufoo.bbs.util.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.gaufoo.bbs.application.ComponentFactory.componentFactory;

public class SchoolHeats {
    private static Logger logger = LoggerFactory.getLogger(SchoolHeats.class);

    public static CreatePostResult createPost(String userToken, PostInfoInput input) {
        logger.debug("createPost, userToken: {}, input: {}", userToken, input);
        try {
            ensurePostInputNonNull(input);

            UserId userId = fetchUserId(userToken);
            PostInfo itemInfo = buildPostItemInfo(userId, input);

            PostId result = publishPost(itemInfo);

            return CreatePostSuccess.of(result.value);

        } catch (AuthenticatorException | PostInputNullException | CreatePostException  e) {
            logger.debug("createPost - failed, error: {}, userToken: {}, input: {}", e.getMessage(), userToken, input);
            return SchoolHeatError.of(e.getMessage());
        }
    }

    private static void ensurePostInputNonNull(PostInfoInput input) {
        if (input.title == null || input.content == null ||
            input.title.isEmpty() || input.content.isEmpty()) {
            throw new PostInputNullException();
        }
    }

    private static UserId fetchUserId(String userToken) throws AuthenticatorException {
        String userIdStr = componentFactory.authenticator.getLoggedUser(UserToken.of(userToken)).userId;
        return UserId.of(userIdStr);
    }

    private static PostInfo buildPostItemInfo(UserId userId, PostInfoInput input) {
        return PostInfo.of(input.title, input.content, userId.value, null, 0, new LinkedList<>(),
                new LinkedList<>(), Instant.now(), Instant.now());
    }

    private static PostId publishPost(PostInfo postInfo) {
        return componentFactory.schoolHeat.publishPost(postInfo)
                .orElseThrow(() -> {
                    logger.debug("publishPost - failed, error: {}, postInfo: {}", "创建帖子失败",  postInfo);
                    return new CreatePostException("创建帖子失败");
                });
    }



    public static ModifyPostResult updatePost(String userToken, String postId, PostInfoInput input) {
        logger.debug("updatePost, userToken: {}, input: {}", userToken, input);
        try {
            UserId userId = fetchUserId(userToken);

            PostInfo oldPostInfo = fetchPostInfo(PostId.of(postId));
            checkPostPermission(oldPostInfo, userId);
            PostInfo newPostInfo = modOldPost(oldPostInfo, input);

            componentFactory.schoolHeat.updatePost(PostId.of(postId), newPostInfo);

            return SchoolHeatSuccess.build();

        } catch (AuthenticatorException | PostNonExistException e) {
            logger.debug("updatePost - failed, error: {}, userToken: {}, input: {}", e.getMessage(), userToken, input);
            return SchoolHeatError.of(e.getMessage());
        }
    }

    private static PostInfo fetchPostInfo(PostId postId) {
        return componentFactory.schoolHeat.postInfo(postId)
                .orElseThrow(() -> {
                    logger.debug("fetchPostInfo - failed, postId: {}", postId);
                    return new PostNonExistException();
                });
    }

    private static void checkPostPermission(PostInfo postInfo, UserId userId) {
        if (!postInfo.author.equals(userId.value)) {
            throw new PostPermissionException();
        }
    }

    private static PostInfo modOldPost(PostInfo postInfo, PostInfoInput input) {
        return postInfo.modTitle(input.title)
                .modContent(input.content)
                .modLatestActiveTime(Instant.now());
    }



    public static ModifyPostResult deletePost(String userToken, String postId) {
        logger.debug("deletePost, userToken: {}, postId: {}", userToken, postId);
        try {
            UserId userId = fetchUserId(userToken);

            PostInfo postToDel = fetchPostInfo(PostId.of(postId));
            checkPostPermission(postToDel, userId);

            removePostAndReplies(postId, postToDel);

            return SchoolHeatSuccess.build();

        } catch (AuthenticatorException | PostNonExistException e) {
            logger.debug("deletePost - failed, error: {}, userToken: {}, postId: {}", e.getMessage(), userToken, postId);
            return SchoolHeatError.of(e.getMessage());
        }
    }

    private static void removePostAndReplies(String postId, PostInfo postInfo) {
        componentFactory.schoolHeat.removePost(PostId.of(postId));
        postInfo.replyIdentifiers.forEach(replyIdentifier ->
                componentFactory.reply.removeReply(ReplyId.of(replyIdentifier))
        );
    }



    public static List<PostItemInfo> allPosts(int skip, int first, SortedBy sortedBy) {
        Comparator<PostInfo> comparator = null;
        switch (sortedBy) {
            case TimeAsc: comparator = PostComparators.comparingTime; break;
            case TimeDes: comparator = PostComparators.comparingTimeReversed; break;
            case HeatAsc: comparator = PostComparators.comparingHeat; break;
            case HeatDes: comparator = PostComparators.comparingHeatReversed; break;
        }
        Stream<PostId> postIds = componentFactory.schoolHeat.allPosts(comparator);

        return convertIdsToItemInfo(postIds)
                .skip(skip).limit(first)
                .collect(Collectors.toList());
    }

    private static Stream<PostItemInfo> convertIdsToItemInfo(Stream<PostId> postIdStream) {
        return postIdStream.map(postId -> Tuple.of(postId, componentFactory.schoolHeat.postInfo(postId)))
                .filter(idInfoTup -> idInfoTup.right.isPresent())
                .map(idInfoTup -> Tuple.of(idInfoTup.left, idInfoTup.right.get()))
                .map(idPostInfoTuple -> constructPostItemInfo(idPostInfoTuple.left, idPostInfoTuple.right));
    }

    private static PostItemInfo constructPostItemInfo(PostId postId, PostInfo postInfo) {
        return new PostItemInfo() {
            @Override
            public String getPostId() {
                return postId.value;
            }
            @Override
            public String getTitle() {
                return Utils.nilStrToEmpty(postInfo.title);
            }
            @Override
            public String getContent() {
                return Utils.nilStrToEmpty(postInfo.content);
            }
            @Override
            public String getAuthor() {
                return postInfo.author;
            }
            @Override
            public String getLatestReplier() {
                return Utils.nilStrToEmpty(postInfo.latestReplier);
            }
            @Override
            public Long getLatestActiveTime() {
                return postInfo.latestActiveTime.toEpochMilli();
            }
            @Override
            public Long getCreateTime() {
                return postInfo.createTime.toEpochMilli();
            }
            @Override
            public Integer getHeat() {
                return postInfo.heat;
            }
            @Override
            public List<ReplyItemInfo> getAllReplies() {
                return postInfo.replyIdentifiers.stream()
                        .map(ReplyId::of)
                        .map(componentFactory.reply::replyInfo)
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .map(SchoolHeats::constructReplyItemInfo)
                        .collect(Collectors.toList());

            }
        };
    }

    private static ReplyItemInfo constructReplyItemInfo(ReplyInfo replyInfo) {
        return new ReplyItemInfo() {
            @Override
            public String getReplyId() {
                return replyInfo.subject;
            }
            @Override
            public String getContent() {
                return Utils.nilStrToEmpty(replyInfo.content);
            }
            @Override
            public String getAuthor() {
                return replyInfo.replier;
            }
            @Override
            public List<CommentItemInfo> getAllComments() {
                return replyInfo.comments.stream()
                        .map(SchoolHeats::constructCommentItemInfo)
                        .collect(Collectors.toList());
            }
        };
    }

    private static CommentItemInfo constructCommentItemInfo(ReplyInfo.Comment comment) {
        return new CommentItemInfo() {
            @Override
            public String getContent() {
                return Utils.nilStrToEmpty(comment.content);
            }
            @Override
            public String getCommentTo() {
                return comment.commentTo;
            }
            @Override
            public String getAuthor() {
                return comment.commentator;
            }
        };
    }

    public enum SortedBy {
        TimeAsc,
        TimeDes,
        HeatAsc,
        HeatDes,
    }

    public interface CommentItemInfo {
        String getContent();
        String getCommentTo();
        String getAuthor();
    }

    public interface ReplyItemInfo {
        String getReplyId();
        String getContent();
        String getAuthor();
        List<CommentItemInfo> getAllComments();
    }

    public interface PostItemInfo {
        String getPostId();
        String getTitle();
        String getContent();
        String getAuthor();
        String getLatestReplier();
        Long getLatestActiveTime();
        Long getCreateTime();
        Integer getHeat();
        List<ReplyItemInfo> getAllReplies();
    }

    public static class PostInfoInput {
        private String title;
        private String content;

        public PostInfoInput() {
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }

    public static class SchoolHeatError implements CreatePostResult, ModifyPostResult {
        private String error;

        public SchoolHeatError(String error) {
            this.error = error;
        }

        public static SchoolHeatError of(String error) {
            return new SchoolHeatError(error);
        }

        public String getError() {
            return error;
        }
    }

    public static class CreatePostSuccess implements CreatePostResult {
        private String postId;

        public CreatePostSuccess(String postId) {
            this.postId = postId;
        }

        public static CreatePostSuccess of(String postId) {
            return new CreatePostSuccess(postId);
        }

        public String getPostId() {
            return postId;
        }
    }


    public interface CreatePostResult {
    }

    public static class SchoolHeatSuccess implements ModifyPostResult {
        private Boolean ok;

        public SchoolHeatSuccess() {
            this.ok = true;
        }

        public static SchoolHeatSuccess build() {
            return new SchoolHeatSuccess();
        }

        public Boolean getOk() {
            return ok;
        }
    }

    public interface ModifyPostResult {
    }

    private static class CreatePostException extends RuntimeException {
        CreatePostException(String errMsg) {
            super(errMsg);
        }
    }

    private static class PostInputNullException extends RuntimeException {
        PostInputNullException() {
            super("标题或内容为空");
        }
    }

    private static class PostNonExistException extends RuntimeException {
        PostNonExistException() {
            super("帖子不存在");
        }
    }

    private static class PostPermissionException extends RuntimeException {
        PostPermissionException() {
            super("无操作权限");
        }
    }

}
