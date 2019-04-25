package com.gaufoo.bbs.application.types;

import java.util.List;

public interface News {

    interface MultiNewsInfos {
        List<NewsInfo> getNewss();
    }
    interface NewsInfoResult {}
    interface CreateNewsResult {}
    interface DeleteNewsResult {}
    interface EditNewsResult {}

    interface NewsInfo extends
            NewsInfoResult,
            CreateNewsResult,
            EditNewsResult,
            DeleteNewsResult
    {
        String getId();
        String getTitle();
        Content getContent();
        Long getPostTime();
        Long getEditTime();
        String getPictureURL();
    }

    class NewsInput {
        public String title;
        public Content content;
        public String pictureBase64;

        @Override
        public String toString() {
            return "NewsInput{" +
                    "title='" + title + '\'' +
                    ", content=" + content +
                    '}';
        }
    }
}
