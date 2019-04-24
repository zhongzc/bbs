package com.gaufoo.bbs.application.types;

import java.util.List;

public interface SchoolHeat {

    interface AllSchoolHeatsResult {}
    interface SchoolHeatInfoResult {}
    interface SchoolHeatsOfAuthorResult {}
    interface SearchSchoolHeatsResult {}
    interface CreateSchoolHeatResult {}
    interface DeleteSchoolHeatResult {}
    interface CreateSchoolHeatCommentResult {}
    interface DeleteSchoolHeatCommentResult {}
    interface CreateSchoolHeatCommentReplyResult {}
    interface DeleteSchoolHeatCommentReplyResult {}

    interface SchoolHeatInfo extends
            SchoolHeatInfoResult,
            CreateSchoolHeatResult,
            DeleteSchoolHeatResult,
            Hot.HotItem,
            Latest.LatestItem
    {
        String getId();
        String getTitle();
        Content getContent();
        PersonalInformation.PersonalInfo getAuthor();
        PersonalInformation.PersonalInfo getLatestCommenter();
        Long getLatestActiveTime();
        Long getCreateTime();
        Integer getHeat();
        Comment.AllComments getAllComments(Long skip, Long first);
    }

    interface MultiSchoolHeats extends
            AllSchoolHeatsResult,
            SearchSchoolHeatsResult,
            SchoolHeatsOfAuthorResult
    {
        Long getTotalCount();
        List<SchoolHeatInfo> getSchoolHeats();
    }

    class SchoolHeatInput {
        public String title;
        public Content content;
    }

    class SchoolHeatCommentInput {
        public String postIdCommenting;
        public Content content;
    }

    class SchoolHeatReplyInput {
        public String commentIdReplying;
        public Content content;
        public String replyTo;
    }
}
