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
        Content getContent();
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
        public Content.ContentInput content;
        public String position;
        public Long time;
        public String lecturer;
        public String note;

        @Override
        public String toString() {
            return "LectureInput{" +
                    "title='" + title + '\'' +
                    ", contentId='" + content + '\'' +
                    ", position='" + position + '\'' +
                    ", time=" + time +
                    ", lecturer='" + lecturer + '\'' +
                    ", note='" + note + '\'' +
                    '}';
        }
    }
}
