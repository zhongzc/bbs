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
        Long getHeat();
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
        public Content.ContentInput content;
    }

    class SchoolHeatCommentInput {
        public String postIdCommenting;
        public Content.ContentInput content;

        @Override
        public String toString() {
            return "SchoolHeatCommentInput{" +
                    "postIdCommenting='" + postIdCommenting + '\'' +
                    ", contentId=" + content +
                    '}';
        }
    }

    class SchoolHeatReplyInput {
        public String postIdReplying;
        public String commentIdReplying;
        public Content.ContentInput content;
        public String replyTo;

        @Override
        public String toString() {
            return "SchoolHeatReplyInput{" +
                    "commentIdReplying='" + commentIdReplying + '\'' +
                    ", contentId=" + content +
                    ", replyTo='" + replyTo + '\'' +
                    '}';
        }
    }
}
