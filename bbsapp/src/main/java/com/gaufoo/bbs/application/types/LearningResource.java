package com.gaufoo.bbs.application.types;

import java.util.List;

public interface LearningResource {
    interface AllLearningResourceResult {}
    interface LearningResourceInfoResult {}
    interface LearningResourcesOfAuthorResult {}
    interface SearchLearningResourcesResult {}
    interface CreateLearningResourceResult {}
    interface DeleteLearningResourceResult {}
    interface CreateLearningResourceCommentResult {}
    interface DeleteLearningResourceCommentResult {}
    interface CreateLearningResourceCommentReplyResult {}
    interface DeleteLearningResourceCommentReplyResult {}

    interface LearningResourceInfo extends
            LearningResourceInfoResult,
            CreateLearningResourceResult,
            DeleteLearningResourceResult,
            Hot.HotItem,
            Latest.LatestItem
    {
        String getId();
        PersonalInformation.PersonalInfo getAuthor();
        String getTitle();
        Content getContent();
        String getCourse();
        String getAttachedFileURL();
        PersonalInformation.PersonalInfo getLatestCommenter();
        Long getLatestActiveTime();
        Long getCreateTime();
        Comment.AllComments getAllComments(Long skip, Long first);
    }

    interface MultiLearningResources extends
            AllLearningResourceResult,
            SearchLearningResourcesResult,
            LearningResourcesOfAuthorResult
    {
        Long getTotalCount();
        List<LearningResourceInfo> getLearningResources();
    }

    class LearningResourceInput {
        public String title;
        public Content content;
        public String course;
        public String base64AttachedFile;
    }

    class LearningResourceCommentInput {
        public String postIdCommenting;
        public Content content;
    }

    class LearningResourceReplyInput {
        public String commentIdReplying;
        public Content content;
        public String replyTo;
    }
}
