package com.gaufoo.bbs.application;

import com.gaufoo.bbs.application.util.Utils;
import com.gaufoo.bbs.components.authenticator.common.UserToken;
import com.gaufoo.bbs.components.authenticator.exceptions.AuthenticatorException;
import com.gaufoo.bbs.components.commentGroup.comment.common.CommentId;
import com.gaufoo.bbs.components.commentGroup.comment.common.CommentInfo;
import com.gaufoo.bbs.components.commentGroup.comment.reply.common.ReplyId;
import com.gaufoo.bbs.components.commentGroup.comment.reply.common.ReplyInfo;
import com.gaufoo.bbs.components._depr_schoolHeat.common.PostComparators;
import com.gaufoo.bbs.components._depr_schoolHeat.common.PostId;
import com.gaufoo.bbs.components._depr_schoolHeat.common.PostInfo;
import com.gaufoo.bbs.components.user.common.UserId;
import com.gaufoo.bbs.util.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.*;
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
            return constructPostItemInfo(result, itemInfo);
        } catch (AuthenticatorException | PostInputNullException | CreatePostException e) {
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
        return PostInfo.of(input.title, input.content, userId.value);
    }
    private static PostId publishPost(PostInfo postInfo) {
        return componentFactory.schoolHeat.publishPost(postInfo)
                .orElseThrow(() -> {
                    logger.debug("publishPost - failed, error: {}, postInfo: {}", "创建帖子失败", postInfo);
                    return new CreatePostException();
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

            return constructPostItemInfo(PostId.of(postId), newPostInfo);

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

            removePostAndComments(postId, postToDel);

            return constructPostItemInfo(PostId.of(postId), postToDel);

        } catch (AuthenticatorException | PostNonExistException e) {
            logger.debug("deletePost - failed, error: {}, userToken: {}, postId: {}", e.getMessage(), userToken, postId);
            return SchoolHeatError.of(e.getMessage());
        }
    }

    private static void removePostAndComments(String postId, PostInfo postInfo) {
        componentFactory.schoolHeat.removePost(PostId.of(postId));
        postInfo.commentIdentifiers.forEach(comId ->
                componentFactory.comment.removeComment(CommentId.of(comId))
        );
    }


    public enum SortedBy {
        TimeAsc,
        TimeDes,
        HeatAsc,
        HeatDes,
    }

    public static AllPostResult allPosts(Long skip, Long first, SortedBy sortedBy) {
        return new AllPostResult() {
            @Override
            public Long getTotalCount() {
                return componentFactory.schoolHeat.allPostsCount();
            }

            @Override
            public List<PostItemInfo> getPostInfos() {
                Comparator<PostInfo> comparator = null;
                Long sk = Optional.ofNullable(skip).orElse(0L);
                Long ft = Optional.ofNullable(first).orElse(getTotalCount());
                SortedBy sb = Optional.ofNullable(sortedBy).orElse(SortedBy.TimeDes);
                switch (sb) {
                    case TimeAsc: comparator = PostComparators.comparingTime;         break;
                    case TimeDes: comparator = PostComparators.comparingTimeReversed; break;
                    case HeatAsc: comparator = PostComparators.comparingHeat;         break;
                    case HeatDes: comparator = PostComparators.comparingHeatReversed; break;
                }
                Stream<PostId> postIds = componentFactory.schoolHeat.allPosts(comparator);
                return convertIdsToItemInfo(postIds)
                        .skip(sk).limit(ft)
                        .collect(Collectors.toList());
            }
        };
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
            public PersonalInformation.PersonalInfo getAuthor() {
                return PersonalInformation.personalInfo(UserId.of(postInfo.author))
                        .orElse(null);
            }
            @Override
            public PersonalInformation.PersonalInfo getLatestCommenter() {
                if (postInfo.latestCommenter == null) return null;
                return PersonalInformation.personalInfo(UserId.of(postInfo.latestCommenter))
                        .orElse(null);
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
            public AllCommentResult getAllComments(Long skip, Long first) {
                return new AllCommentResult() {
                    private Stream<CommentItemInfo> comments = postInfo.commentIdentifiers.stream().map(commentIdVal -> {
                        CommentId commentId = CommentId.of(commentIdVal);
                        Optional<CommentInfo> oReplyInfo = componentFactory.comment.commentInfo(commentId);
                        return oReplyInfo.map(replyInfo -> constructCommentItemInfo(commentId, replyInfo))
                                .orElse(null);
                    }).filter(Objects::nonNull);

                    @Override
                    public Long getTotalCount() {
                        return postInfo.commentCount;
                    }

                    @Override
                    public List<CommentItemInfo> getComments() {
                        Long sk = Optional.ofNullable(skip).orElse(0L);
                        Long ft = Optional.ofNullable(first).orElse(getTotalCount());
                        return comments.skip(sk).limit(ft).collect(Collectors.toList());
                    }
                };
            }
        };
    }

    private static CommentItemInfo constructCommentItemInfo(CommentId commentId, CommentInfo commentInfo) {
        return new CommentItemInfo() {
            @Override
            public String getCommentId() {
                return commentId.value;
            }

            @Override
            public String getPostIdCommenting() {
                return commentInfo.subject;
            }

            @Override
            public String getContent() {
                return Utils.nilStrToEmpty(commentInfo.content);
            }

            @Override
            public PersonalInformation.PersonalInfo getAuthor() {
                return PersonalInformation.personalInfo(UserId.of(commentInfo.commenter))
                        .orElse(null);
            }

            @Override
            public List<ReplyItemInfo> getAllReplies() {
                return commentInfo.replies.stream()
                        .map(componentFactory.comment::replyInfo)
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .map(SchoolHeats::constructReplyItemInfo)
                        .collect(Collectors.toList());
            }
        };
    }

    private static ReplyItemInfo constructReplyItemInfo(ReplyInfo reply) {
        return new ReplyItemInfo() {
            @Override
            public String getContent() {
                return Utils.nilStrToEmpty(reply.content);
            }

            @Override
            public PersonalInformation.PersonalInfo getReplyTo() {
                if (reply.replyTo == null) return null;
                return PersonalInformation.personalInfo(UserId.of(reply.replyTo))
                        .orElse(null);
            }

            @Override
            public PersonalInformation.PersonalInfo getAuthor() {
                return PersonalInformation.personalInfo(UserId.of(reply.replier))
                        .orElse(null);
            }
        };
    }


    public static PostInfoResult postInfo(String postIdStr) {
        logger.debug("allPost, postId: {}", postIdStr);
        try {
            PostId postId = PostId.of(postIdStr);
            PostInfo postInfo = fetchPostInfo(postId);
            return constructPostItemInfo(postId, postInfo);
        } catch (PostNonExistException e) {
            logger.debug("allPost - failed, error: {}, postId: {}", e.getMessage(), postIdStr);
            return SchoolHeatError.of(e.getMessage());
        }
    }


    private interface UndoFunction {
        void undo();
    }

    public static CreateCommentResult createComment(String userToken, CommentInfoInput input) {
        logger.debug("createComment, input: {}", input);
        List<UndoFunction> undoFunctions = new LinkedList<>();
        try {
            UserId userId = fetchUserId(userToken);

            CommentInfo commentInfo = buildCommentInfo(userId, input);
            Optional<CommentId> commentId = componentFactory.comment.comment(commentInfo);
            if (!commentId.isPresent()) {
                logger.debug("createComment - failed, error: {}, input: {}", "添加评论失败", input);
                return SchoolHeatError.of("添加评论失败");
            }
            undoFunctions.add(() -> componentFactory.comment.removeComment(commentId.get()));

            PostId postId = PostId.of(input.postIdToComment);
            componentFactory.schoolHeat.addComment(postId, commentId.get().value);
            undoFunctions.add(() -> componentFactory.schoolHeat.removeComment(postId, commentId.get().value));

            return constructCommentItemInfo(commentId.get(), commentInfo);

        } catch (AuthenticatorException e) {
            logger.debug("createComment - failed, error: {}, input: {}", e.getMessage(), input);
            undoFunctions.forEach(UndoFunction::undo);
            return SchoolHeatError.of(e.getMessage());
        }
    }

    private static CommentInfo buildCommentInfo(UserId userId, CommentInfoInput input) {
        return CommentInfo.of(input.postIdToComment, input.content, userId.value);
    }


    public static CreateReplyResult createReply(String userToken, ReplyInfoInput input) {
        logger.debug("createReply, input: {}", input);
        try {
            UserId userId = fetchUserId(userToken);

            ReplyInfo reply = ReplyInfo.of(userId.value, input.content, input.commentIdToReply);
            Optional<ReplyId> rpyId = componentFactory.comment.reply(CommentId.of(input.commentIdToReply), reply);
            if (!rpyId.isPresent()) return SchoolHeatError.of("添加回复失败");

            CommentInfo replyInfo = fetchReplyInfo(CommentId.of(input.commentIdToReply));
            PostId postId = PostId.of(replyInfo.subject);
            PostInfo postInfo = fetchPostInfo(postId);

            PostItemInfo postItemInfo = constructPostItemInfo(postId, postInfo);

            return constructReplyItemInfo(reply);

        } catch (AuthenticatorException | CommentNonExistException e) {
            logger.debug("createReply - failed, error: {}, input: {}", e.getMessage(), input);
            return SchoolHeatError.of(e.getMessage());
        }
    }

    private static CommentInfo fetchReplyInfo(CommentId commentId) {
        return componentFactory.comment.commentInfo(commentId)
                .orElseThrow(() -> {
                    logger.debug("fetchReplyInfo - failed, replyId: {}", commentId);
                    return new CommentNonExistException();
                });
    }


    // for test
    public static void reset() {
        componentFactory.schoolHeat.allPosts(PostComparators.comparingTime).forEach(postId -> {
            PostInfo postInfo = componentFactory.schoolHeat.postInfo(postId).orElse(null);
            componentFactory.schoolHeat.removePost(postId);

            if (postInfo == null) return;
            postInfo.commentIdentifiers.forEach(replyId ->
                    componentFactory.comment.removeComment(CommentId.of(replyId))
            );
        });

    }

    public interface ReplyItemInfo extends CreateReplyResult {
        String getContent();
        PersonalInformation.PersonalInfo getReplyTo();
        PersonalInformation.PersonalInfo getAuthor();
    }

    public interface CommentItemInfo extends CreateCommentResult {
        String getCommentId();
        String getPostIdCommenting();
        String getContent();
        PersonalInformation.PersonalInfo getAuthor();
        List<ReplyItemInfo> getAllReplies();
    }

    public interface PostItemInfo extends CreatePostResult, ModifyPostResult, PostInfoResult {
        String getPostId();
        String getTitle();
        String getContent();
        PersonalInformation.PersonalInfo getAuthor();
        PersonalInformation.PersonalInfo getLatestCommenter();
        Long getLatestActiveTime();
        Long getCreateTime();
        Integer getHeat();
        AllCommentResult getAllComments(Long skip, Long first);
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

        @Override
        public String toString() {
            return "title: " + title + ", content: " + content;
        }
    }

    public static class CommentInfoInput {
        private String postIdToComment;
        private String content;
        public CommentInfoInput() {
        }
        public void setPostIdToComment(String postIdToComment) {
            this.postIdToComment = postIdToComment;
        }
        public void setContent(String content) {
            this.content = content;
        }
        @Override
        public String toString() {
            return "postIdToComment: " + postIdToComment + ", content: " + content;
        }
    }

    public static class ReplyInfoInput {
        private String commentIdToReply;
        private String content;
        public ReplyInfoInput() {
        }
        public void setCommentIdToReply(String commentIdToReply) {
            this.commentIdToReply = commentIdToReply;
        }
        public void setContent(String content) {
            this.content = content;
        }
        @Override
        public String toString() {
            return "commentIdToReply: " + commentIdToReply + ", content: " + content;
        }
    }

    public interface AllPostResult {
        Long getTotalCount();
        List<PostItemInfo> getPostInfos();
    }

    public interface AllCommentResult {
        Long getTotalCount();
        List<CommentItemInfo> getComments();
    }

    public static class SchoolHeatError implements CreatePostResult, ModifyPostResult, CreateCommentResult, CreateReplyResult, PostInfoResult {
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

    public interface PostInfoResult      { }
    public interface CreatePostResult    { }
    public interface ModifyPostResult    { }
    public interface CreateCommentResult { }
    public interface CreateReplyResult   { }

    private static class CreatePostException     extends RuntimeException { CreatePostException()     { super("创建帖子失败"); }}
    private static class PostInputNullException  extends RuntimeException { PostInputNullException()  { super("标题或内容为空"); }}
    private static class PostNonExistException   extends RuntimeException { PostNonExistException()   { super("帖子不存在"); }}
    private static class CommentNonExistException extends RuntimeException { CommentNonExistException()  { super("回复不存在"); }}
    private static class PostPermissionException extends RuntimeException { PostPermissionException() { super("无操作权限"); }}

}
