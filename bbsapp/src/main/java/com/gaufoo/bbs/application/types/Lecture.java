package com.gaufoo.bbs.application.types;

import java.util.List;

public interface Lecture {

    interface AllLecturesResult {}
    interface LectureInfoResult {}
    interface SearchLecturesResult {}
    interface CreateLectureResult {}
    interface EditLectureResult {}
    interface DeleteLectureResult {}

    interface LectureInfo extends
            LectureInfoResult,
            CreateLectureResult,
            EditLectureResult,
            DeleteLectureResult
    {
        String getId();
        String getTitle();
        String getContent();
        String getPosition();
        Long getTime();
        String getLecturer();
        String getNote();
    }

    interface MultiLectures extends
            AllLecturesResult,
            SearchLecturesResult
    {
        Long getTotalCount();
        List<LectureInfo> getLectures();
    }

    class LectureInput {
        public String title;
        public String content;
        public String position;
        public Long time;
        public String lecturer;
        public String note;
    }
}
