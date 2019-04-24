package com.gaufoo.bbs.application.types;

import java.util.List;

public interface Comment {
    interface AllComments {
        Long getTotalCount();
        List<CommentInfo> getComments();
    }

    interface CommentInfo extends
            SchoolHeat.CreateSchoolHeatCommentResult,
            SchoolHeat.DeleteSchoolHeatCommentResult,
            Entertainment.CreateEntertainmentCommentResult,
            Entertainment.DeleteEntertainmentCommentResult,
            LearningResource.CreateLearningResourceCommentResult,
            LearningResource.DeleteLearningResourceCommentResult
    {
        String getId();
        Content getContent();
        PersonalInformation.PersonalInfo getAuthor();
        AllReplies getAllReplies();
    }

    interface AllReplies {
        Long getTotalCount();
        List<ReplyInfo> getReplies();
    }

    interface ReplyInfo extends
            SchoolHeat.CreateSchoolHeatCommentReplyResult,
            SchoolHeat.DeleteSchoolHeatCommentReplyResult,
            Entertainment.CreateEntertainmentCommentReplyResult,
            Entertainment.DeleteEntertainmentCommentReplyResult,
            LearningResource.CreateLearningResourceCommentReplyResult,
            LearningResource.DeleteLearningResourceCommentReplyResult
    {
        String getId();
        Content getContent();
        PersonalInformation.PersonalInfo getAuthor();
        PersonalInformation.PersonalInfo getReplyTo();
    }
}
